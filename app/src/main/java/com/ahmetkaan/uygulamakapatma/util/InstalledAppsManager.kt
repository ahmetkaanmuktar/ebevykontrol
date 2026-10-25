package com.ahmetkaan.uygulamakapatma.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap
import com.ahmetkaan.uygulamakapatma.data.local.entity.AppLimit
import java.util.concurrent.ConcurrentHashMap

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Bitmap,
    val todayUsageMinutes: Int = 0,
    val limit: AppLimit? = null
)

/**
 * Cihazdaki başlatılabilir (Launcher) uygulamaları Google Play uyumlu sorgulayan sınıf.
 * Düşük bellekli (3-4 GB RAM) cihazlarda OOM (OutOfMemory) hatasını önlemek için
 * 96x96 sıkıştırılmış ikon önbelleği (Icon Cache) kullanır.
 */
object InstalledAppsManager {

    private val iconCache = ConcurrentHashMap<String, Bitmap>()

    fun getInstalledLauncherApps(
        context: Context,
        savedLimits: Map<String, AppLimit> = emptyMap(),
        todayUsages: Map<String, Int> = emptyMap()
    ): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = try {
            pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        } catch (e: Exception) {
            emptyList()
        }

        val apps = mutableListOf<AppInfo>()
        val seenPackages = mutableSetOf<String>()

        for (info in resolveInfos) {
            val pkg = info.activityInfo.packageName
            if (seenPackages.contains(pkg)) continue
            seenPackages.add(pkg)

            // Kendi uygulamamızı ve kritik sistem uygulamalarını hariç tut
            if (WhitelistManager.isWhitelisted(context, pkg)) continue

            val appName = try {
                info.loadLabel(pm).toString()
            } catch (e: Exception) {
                pkg
            }

            // İkonu önbellekten al veya RAM dostu (96x96) üret
            val iconBitmap = iconCache.getOrPut(pkg) {
                val iconDrawable = try {
                    info.loadIcon(pm)
                } catch (e: Exception) {
                    pm.defaultActivityIcon
                }
                drawableToScaledBitmap(iconDrawable)
            }

            val usageMinutes = todayUsages[pkg] ?: UsageTrackerManager.getTodayUsageMinutes(context, pkg)
            val limit = savedLimits[pkg]

            apps.add(
                AppInfo(
                    packageName = pkg,
                    appName = appName,
                    icon = iconBitmap,
                    todayUsageMinutes = usageMinutes,
                    limit = limit
                )
            )
        }

        return apps.sortedWith(
            compareByDescending<AppInfo> { it.limit != null }
                .thenByDescending { it.todayUsageMinutes }
                .thenBy { it.appName.lowercase() }
        )
    }

    private fun drawableToScaledBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            val original = drawable.bitmap
            if (original.width <= 96 && original.height <= 96) return original
            return Bitmap.createScaledBitmap(original, 96, 96, true)
        }
        val targetSize = 96
        val bitmap = createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
