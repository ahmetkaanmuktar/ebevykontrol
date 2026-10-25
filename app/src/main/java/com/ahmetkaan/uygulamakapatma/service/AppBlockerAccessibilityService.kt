package com.ahmetkaan.uygulamakapatma.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import com.ahmetkaan.uygulamakapatma.ParentalControlApp
import com.ahmetkaan.uygulamakapatma.ui.block.BlockOverlayActivity
import com.ahmetkaan.uygulamakapatma.util.NotificationHelper
import com.ahmetkaan.uygulamakapatma.util.UsageTrackerManager
import com.ahmetkaan.uygulamakapatma.util.WhitelistManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Gerçek zamanlı uygulama tespit ve süre denetim servisi.
 * - Ön plandaki uygulamayı `TYPE_WINDOW_STATE_CHANGED` ile dinler.
 * - Acil arama, telefon, SMS ve sistem paketlerini (Whitelist) asla engellemez.
 * - Süresi dolan veya kalıcı engellenen uygulamalarda `BlockOverlayActivity` başlatır;
 *   gerekirse `GLOBAL_ACTION_HOME` yedeğini devreye sokar.
 * - Servis kapatıldığında veya kesintiye uğradığında ebeveyne yerel bildirim fırlatır.
 */
class AppBlockerAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Arka arkaya aynı paket için tekrarlayan blok tetiklemelerini önlemek için son engelleme zamanı
    private var lastBlockedPackage: String? = null
    private var lastBlockedTime: Long = 0L

    // Ön plandaki kısıtlı uygulamayı canlı izlemek için aktif zamanlayıcı işi
    private var activeMonitoringJob: kotlinx.coroutines.Job? = null
    private var currentMonitoredPackage: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val targetPackage = event.packageName?.toString() ?: return

        // 1. Kritik Whitelist Kontrolü (Acil arama, Sistem, Kendi uygulamamız vb.)
        if (WhitelistManager.isWhitelisted(this, targetPackage)) {
            // İzinli bir uygulamaya geçildiyse önceki aktif izleme görevini durdur
            activeMonitoringJob?.cancel()
            activeMonitoringJob = null
            currentMonitoredPackage = null
            return
        }

        // Debounce: Aynı paket 1.5 saniye içinde tekrar tetiklenmişse atla
        val now = SystemClock.uptimeMillis()
        if (targetPackage == lastBlockedPackage && (now - lastBlockedTime) < 1500L) {
            return
        }

        serviceScope.launch {
            val isBlocked = checkAndBlockIfExceeded(targetPackage)
            
            // Eğer henüz engellenmediyse ve bu paket ön planda kalmaya devam ediyorsa
            // Çocuk pencereyi değiştirmeden video/oyun oynarken süre bittiğinde yakalamak için canlı izleme başlat
            if (!isBlocked && currentMonitoredPackage != targetPackage) {
                activeMonitoringJob?.cancel()
                currentMonitoredPackage = targetPackage
                activeMonitoringJob = serviceScope.launch {
                    while (isActive && currentMonitoredPackage == targetPackage) {
                        kotlinx.coroutines.delay(15_000L) // 15 saniyede bir hafif kontrol (pil dostu)
                        val blocked = checkAndBlockIfExceeded(targetPackage)
                        if (blocked) {
                            activeMonitoringJob = null
                            currentMonitoredPackage = null
                            break
                        }
                    }
                }
            }
        }
    }

    private suspend fun checkAndBlockIfExceeded(targetPackage: String): Boolean {
        val app = applicationContext as? ParentalControlApp ?: return false
        val limit = app.database.appLimitDao().getLimitByPackageName(targetPackage) ?: return false

        // Bugün harcanan dinamik süreyi hesapla
        val usedMinutes = UsageTrackerManager.getTodayUsageMinutes(this, targetPackage, forceFresh = true)
        val isExtensionToday = UsageTrackerManager.isToday(limit.extensionDateMillis)
        val effectiveLimit = limit.getEffectiveDailyLimitMinutes(isExtensionToday)

        val isExceeded = limit.isPermanentlyBlocked || (usedMinutes >= effectiveLimit)

        if (isExceeded) {
            lastBlockedPackage = targetPackage
            lastBlockedTime = SystemClock.uptimeMillis()

            try {
                // 1. Kilit Ekranını Başlat
                BlockOverlayActivity.start(
                    context = this,
                    packageName = targetPackage,
                    appName = getAppLabel(targetPackage),
                    usedMinutes = usedMinutes,
                    limitMinutes = effectiveLimit
                )
            } catch (e: Exception) {
                // Arka plandan aktivite başlatma kısıtlamasına takılırsa güvenli yedek: HOME tuşunu tetikle
                performGlobalAction(GLOBAL_ACTION_HOME)
            }
            return true
        }
        return false
    }

    private fun getAppLabel(pkg: String): String {
        return try {
            val pm = packageManager
            val info = pm.getApplicationInfo(pkg, 0)
            pm.getApplicationLabel(info).toString()
        } catch (e: Exception) {
            pkg
        }
    }

    override fun onInterrupt() {
        // Servis kesintiye uğradığında ebeveyne haber ver
        NotificationHelper.showServiceDisabledNotification(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Servis kapatıldığında bildirim fırlat ve coroutine scope'u temizle
        NotificationHelper.showServiceDisabledNotification(this)
        serviceScope.cancel()
    }
}
