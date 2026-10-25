package com.ahmetkaan.uygulamakapatma.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

/**
 * Google Play uyumlu izin kontrol sınıfı.
 * Device Admin (Cihaz Yöneticisi) kaldırılmıştır.
 * Yalnızca Kullanım Verileri, Üzerinde Gösterme ve Erişilebilirlik kontrolleri yapılır.
 */
object PermissionChecker {

    /**
     * Kullanım Verilerine Erişim (PACKAGE_USAGE_STATS) iznini kontrol eder.
     */
    fun hasUsageStatsPermission(context: Context): Boolean {
        return try {
            val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOpsManager.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Diğer uygulamaların üzerinde gösterme (SYSTEM_ALERT_WINDOW) iznini kontrol eder.
     */
    fun hasOverlayPermission(context: Context): Boolean {
        return try {
            Settings.canDrawOverlays(context)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Uygulamanın Erişilebilirlik Servisi'nin (AppBlockerAccessibilityService) açık olup olmadığını kontrol eder.
     */
    fun hasAccessibilityPermission(context: Context): Boolean {
        return try {
            val accessibilityManager =
                context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

            val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_GENERIC
            )

            val expectedComponentName = ComponentName(
                context.packageName,
                "com.ahmetkaan.uygulamakapatma.service.AppBlockerAccessibilityService"
            ).flattenToString()

            enabledServices.any { serviceInfo ->
                serviceInfo.resolveInfo?.serviceInfo?.let { info ->
                    ComponentName(info.packageName, info.name).flattenToString() == expectedComponentName
                } ?: false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Pil optimizasyonu muafiyeti (Doze mode muafiyeti) verilip verilmediğini kontrol eder.
     * Üretici kısıtlamalarına karşı servisin canlı kalması için gereklidir.
     */
    fun isBatteryOptimizationIgnored(context: Context): Boolean {
        return try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Android 13+ (API 33+) bildirim izninin verilip verilmediğini kontrol eder.
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Zorunlu 3 sistem izninin (Kullanım, Overlay, Erişilebilirlik) verilip verilmediğini kontrol eder.
     */
    fun areCorePermissionsGranted(context: Context): Boolean {
        return hasUsageStatsPermission(context) &&
                hasOverlayPermission(context) &&
                hasAccessibilityPermission(context)
    }

    /**
     * Uygulamanın Google Play Store dışından (APK / Sideload) yüklenip yüklenmediğini kontrol eder.
     */
    fun isSideloaded(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            val installer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pm.getInstallSourceInfo(context.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                pm.getInstallerPackageName(context.packageName)
            }
            installer != "com.android.vending"
        } catch (e: Exception) {
            true // Hata durumunda güvenlik için sideloaded kabul et
        }
    }

    /**
     * Android 13+ (API 33+) cihazlarda ve APK kurulumlarında Erişilebilirlik ayarının
     * kilitli ("Kısıtlanmış Ayar") gelme ihtimalini kontrol eder.
     */
    fun mayNeedRestrictedSettingsUnlock(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isSideloaded(context)
    }
}
