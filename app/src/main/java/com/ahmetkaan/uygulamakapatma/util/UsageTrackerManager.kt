package com.ahmetkaan.uygulamakapatma.util

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.SystemClock
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * Android 15 ve Google Play uyumlu dinamik ekran süresi hesaplayıcı.
 * - Saat dilimi ve DST duyarlı gün başlangıcı hesabı.
 * - Saat hilesi koruması (elapsedRealtime karşılaştırması).
 * - CPU/Pil tasarrufu için 20 saniyelik hafif TTL önbelleği (düşük segment telefonlar için).
 */
object UsageTrackerManager {

    // Paket bazlı hafif önbellek: packageName -> (Hesaplandığı elapsedRealtime, Dakika)
    private val usageCache = ConcurrentHashMap<String, Pair<Long, Int>>()
    private const val CACHE_TTL_MS = 20_000L // 20 saniye

    // Saat manipülasyonu tespiti için son kayıtlar
    @Volatile
    private var lastRecordedWallTime: Long = System.currentTimeMillis()
    @Volatile
    private var lastRecordedBootTime: Long = 0L

    private fun safeBootTime(): Long {
        return try {
            SystemClock.elapsedRealtime()
        } catch (_: Throwable) {
            0L
        }
    }

    /**
     * Verilen saat dilimine göre bugünün başlangıcını (00:00:00.000) Epoch millis olarak döner.
     */
    fun getStartOfDayMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
        return ZonedDateTime.now(zoneId)
            .toLocalDate()
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Belirli bir uygulamanın bugünkü kullanım süresini (dakika) döner.
     * Hızlı pencere değişimlerinde CPU yormamak için 20 saniyelik TTL önbelleği kullanır.
     */
    fun getTodayUsageMinutes(context: Context, packageName: String, forceFresh: Boolean = false): Int {
        val nowBoot = safeBootTime()

        if (!forceFresh) {
            val cached = usageCache[packageName]
            if (cached != null && (nowBoot - cached.first) < CACHE_TTL_MS) {
                return cached.second
            }
        }

        val millis = getTodayUsageMillis(context, packageName)
        val minutes = (millis / (1000 * 60)).toInt()

        usageCache[packageName] = Pair(nowBoot, minutes)
        return minutes
    }

    /**
     * Belirli bir uygulamanın bugünkü kullanım süresini milisaniye cinsinden hesaplar.
     */
    fun getTodayUsageMillis(
        context: Context,
        packageName: String,
        zoneId: ZoneId = ZoneId.systemDefault(),
        currentTimeMillis: Long = System.currentTimeMillis()
    ): Long {
        checkClockTampering()

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return 0L

        val startOfDay = getStartOfDayMillis(zoneId)
        if (currentTimeMillis <= startOfDay) return 0L

        return try {
            val usageEvents = usageStatsManager.queryEvents(startOfDay, currentTimeMillis)
            var totalTime = 0L
            var lastResumeTime: Long? = null

            val event = UsageEvents.Event()
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.packageName != packageName) continue

                when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        lastResumeTime = event.timeStamp
                    }
                    UsageEvents.Event.ACTIVITY_PAUSED -> {
                        if (lastResumeTime != null) {
                            totalTime += (event.timeStamp - lastResumeTime)
                            lastResumeTime = null
                        } else {
                            // Gece yarısından önce açılmış ve gece yarısından sonra kapatılmış durum
                            totalTime += (event.timeStamp - startOfDay)
                        }
                    }
                }
            }

            // Uygulama şu an hala ön plandaysa açık geçen süreyi ekle
            if (lastResumeTime != null) {
                totalTime += (currentTimeMillis - lastResumeTime)
            }

            // Event logları boş veya eksikse INTERVAL_DAILY ile yedekle
            if (totalTime == 0L) {
                val statsList = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    startOfDay,
                    currentTimeMillis
                )
                val appStat = statsList.firstOrNull { it.packageName == packageName }
                totalTime = appStat?.totalTimeInForeground ?: 0L
            }

            totalTime.coerceAtLeast(0L)
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Çocuğun cihaz saatini geriye veya ileriye alıp almadığını kontrol eder.
     * Cihaz donanım sayacı (elapsedRealtime) ile duvar saati arasındaki fark kontrol edilir.
     */
    private fun checkClockTampering() {
        val currentWall = System.currentTimeMillis()
        val currentBoot = safeBootTime()

        val wallDelta = currentWall - lastRecordedWallTime
        val bootDelta = currentBoot - lastRecordedBootTime

        // Duvar saati ile açılış sayacı arasında 1 saati aşan tutarsızlık varsa önbelleği temizle
        if (kotlin.math.abs(wallDelta - bootDelta) > (60 * 60 * 1000L)) {
            usageCache.clear()
        }

        lastRecordedWallTime = currentWall
        lastRecordedBootTime = currentBoot
    }

    /**
     * Cihazdaki tüm uygulamaların bugünkü kullanım sürelerini harita olarak döner.
     */
    fun getAllAppsUsageToday(context: Context): Map<String, Int> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return emptyMap()

        val startOfDay = getStartOfDayMillis()
        val now = System.currentTimeMillis()

        return try {
            val statsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startOfDay,
                now
            )
            val result = statsList.associate { it.packageName to (it.totalTimeInForeground / (1000 * 60)).toInt() }

            // Önbelleği güncelle
            val nowBoot = safeBootTime()
            result.forEach { (pkg, mins) ->
                usageCache[pkg] = Pair(nowBoot, mins)
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }

    /**
     * Verilen tarihin bugün olup olmadığını denetler.
     */
    fun isToday(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
        if (epochMillis <= 0L) return false
        val givenDate = ZonedDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(epochMillis),
            zoneId
        ).toLocalDate()
        val today = LocalDate.now(zoneId)
        return givenDate == today
    }
}
