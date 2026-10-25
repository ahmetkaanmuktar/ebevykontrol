package com.ahmetkaan.uygulamakapatma.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

/**
 * Google Play uyumlu sistem ayarları ekranlarını açma yardımcısı.
 * Device Admin kaldırılmıştır; Kullanım Verileri, Overlay, Erişilebilirlik
 * ve Pil Optimizasyonu muafiyeti için güvenli intent fırlatır.
 */
object PermissionLauncher {

    /**
     * Kullanım Verilerine Erişim (Usage Access) ayarlarını açar.
     */
    fun openUsageStatsSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            // Android 11+ için paket adı ekleyerek doğrudan uygulamaya odaklanmayı dene
            data = Uri.parse("package:${context.packageName}")
        }
        safeStartActivity(context, intent) {
            // Doğrudan paket linki açılamazsa genel listeyi aç
            safeStartActivity(context, Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) {
                showFallbackMessage(context, "Kullanım verilerine erişim")
            }
        }
    }

    /**
     * Diğer Uygulamaların Üzerinde Gösterme (Overlay) ayarlarını açar.
     */
    fun openOverlaySettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        safeStartActivity(context, intent) {
            val fallbackIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            safeStartActivity(context, fallbackIntent) {
                showFallbackMessage(context, "Üzerinde gösterme izni")
            }
        }
    }

    /**
     * Erişilebilirlik Servisi ayarlarını açar.
     */
    fun openAccessibilitySettings(context: Context) {
        safeStartActivity(context, Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) {
            showFallbackMessage(context, "Erişilebilirlik servisi")
        }
    }

    /**
     * Pil Optimizasyonu muafiyeti ayarları ekranını açar.
     * Google Play politikaları gereğince doğrudan sistem ayarları listesine yönlendirir.
     */
    fun openBatteryOptimizationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        safeStartActivity(context, intent) {
            // Desteklenmeyen cihazlarda doğrudan uygulama bilgisi ekranına yönlendir
            openAppDetails(context)
        }
    }

    /**
     * Intent'i güvenli şekilde başlatır. Hata durumunda onError callback'ini tetikler.
     */
    private fun safeStartActivity(
        context: Context,
        intent: Intent,
        onError: () -> Unit
    ) {
        try {
            if (context !is android.app.Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            onError()
        } catch (e: SecurityException) {
            onError()
        } catch (e: Exception) {
            onError()
        }
    }

    /**
     * Cihaz Xiaomi / Redmi / POCO (MIUI veya HyperOS) ailesinden mi?
     */
    fun isXiaomiFamily(): Boolean {
        val m = android.os.Build.MANUFACTURER.lowercase()
        return m.contains("xiaomi") || m.contains("redmi") || m.contains("poco")
    }

    /**
     * Xiaomi "Otomatik başlatma" ekranını açar. Bulunamazsa uygulama bilgisi ekranına düşer.
     */
    fun openAutoStartSettings(context: Context) {
        val intent = Intent().apply {
            component = android.content.ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        }
        safeStartActivity(context, intent) { openAppDetails(context) }
    }

    /**
     * Xiaomi "Diğer izinler" ekranını açar (Arka planda açılır pencere göster vb.).
     */
    fun openMiuiAppPermissions(context: Context) {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR").apply {
            setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            putExtra("extra_pkgname", context.packageName)
        }
        safeStartActivity(context, intent) { openAppDetails(context) }
    }

    /**
     * Genel Android "Uygulama bilgisi" ekranı (tüm üreticilerde çalışır).
     */
    fun openAppDetails(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        safeStartActivity(context, intent) {
            showFallbackMessage(context, "Uygulama bilgisi")
        }
    }

    private fun showFallbackMessage(context: Context, permissionName: String) {
        Toast.makeText(
            context,
            "$permissionName ayarları doğrudan açılamadı.\n" +
                    "Lütfen telefonunuzun Ayarlar menüsünden ilgili izni manuel olarak etkinleştirin.",
            Toast.LENGTH_LONG
        ).show()
    }
}
