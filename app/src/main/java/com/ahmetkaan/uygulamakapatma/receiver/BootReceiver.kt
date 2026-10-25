package com.ahmetkaan.uygulamakapatma.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ahmetkaan.uygulamakapatma.util.NotificationHelper
import com.ahmetkaan.uygulamakapatma.util.PermissionChecker

/**
 * Cihaz yeniden başladığında (BOOT_COMPLETED) tetiklenen alıcı.
 * Erişilebilirlik servisinin açık olup olmadığını denetler;
 * kapalıysa ebeveyne durum bildirimi gösterir.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            val isServiceActive = PermissionChecker.hasAccessibilityPermission(context)
            if (!isServiceActive && PermissionChecker.hasUsageStatsPermission(context)) {
                NotificationHelper.showServiceDisabledNotification(context)
            }
        }
    }
}
