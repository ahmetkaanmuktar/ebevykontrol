package com.ahmetkaan.uygulamakapatma.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony
import android.telecom.TelecomManager

/**
 * Hiçbir koşulda engellenmemesi gereken kritik sistem ve iletişim uygulamalarını yöneten sınıf.
 * Acil arama, telefon, SMS, ana ekran (launcher) ve sistem arayüzü asla engellenemez.
 */
object WhitelistManager {

    // Sabit sistem bileşenleri
    private val HARDCODED_CRITICAL_PACKAGES = setOf(
        "com.android.systemui",              // Sistem arayüzü (Bildirimler, gezinme çubuğu vb.)
        "com.android.settings",              // Ayarlar (Google Play politikası gereği engellenmez)
        "com.google.android.packageinstaller", // Paket yükleyici
        "com.android.packageinstaller",
        "com.google.android.permissioncontroller", // İzin yöneticisi
        "com.android.permissioncontroller",
        "com.android.phone",                 // Telefon radyo servisi
        "com.android.server.telecom"         // Acil durum ve arama yönlendirme servisi
    )

    /**
     * Verilen paket adının engellemeden muaf olup olmadığını kontrol eder.
     * Acil arama ve temel sistem servisleri daima 'true' döner.
     */
    fun isWhitelisted(context: Context, packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) return true

        // 1. Kendi uygulamamız
        if (packageName == context.packageName) return true

        // 2. Sabit sistem paketleri
        if (HARDCODED_CRITICAL_PACKAGES.contains(packageName)) return true

        // 3. Varsayılan Launcher (Ana Ekran)
        if (isDefaultLauncher(context, packageName)) return true

        // 4. Varsayılan Telefon / Dialer / Acil Çağrı
        if (isDefaultDialerOrEmergency(context, packageName)) return true

        // 5. Varsayılan SMS uygulaması
        if (isDefaultSms(context, packageName)) return true

        return false
    }

    /**
     * Cihazın varsayılan ana ekranı (Launcher) mı kontrol eder.
     */
    private fun isDefaultLauncher(context: Context, packageName: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
            }
            val resolveInfo = context.packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            resolveInfo?.activityInfo?.packageName == packageName
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Cihazın varsayılan arama (Dialer) veya acil durum arama uygulaması mı kontrol eder.
     */
    private fun isDefaultDialerOrEmergency(context: Context, packageName: String): Boolean {
        return try {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
            val defaultDialer = telecomManager?.defaultDialerPackage
            if (defaultDialer == packageName) return true

            // DIAL intent'ini karşılayan varsayılan uygulama
            val dialIntent = Intent(Intent.ACTION_DIAL)
            val resolveInfo = context.packageManager.resolveActivity(
                dialIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            resolveInfo?.activityInfo?.packageName == packageName
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Cihazın varsayılan SMS uygulaması mı kontrol eder.
     */
    private fun isDefaultSms(context: Context, packageName: String): Boolean {
        return try {
            val defaultSms = Telephony.Sms.getDefaultSmsPackage(context)
            defaultSms == packageName
        } catch (e: Exception) {
            false
        }
    }
}
