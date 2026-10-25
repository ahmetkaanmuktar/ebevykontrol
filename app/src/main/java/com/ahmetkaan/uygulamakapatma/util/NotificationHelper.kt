package com.ahmetkaan.uygulamakapatma.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ahmetkaan.uygulamakapatma.MainActivity
import com.ahmetkaan.uygulamakapatma.R

/**
 * Ebeveyn uyarı bildirimlerini yöneten yardımcı sınıf.
 * Servisin durdurulması veya kısıtlamaların devre dışı kalması durumunda
 * ebeveyne sesli ve öncelikli yerel bildirim fırlatır.
 */
object NotificationHelper {

    private const val CHANNEL_ID = "parental_control_alerts"
    private const val NOTIFICATION_ID_SERVICE_STOPPED = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Ebeveyn Denetimi Uyarıları"
            val descriptionText = "Servis durumu ve süre aşımı uyarıları"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Erişilebilirlik servisi kapatıldığında veya üretici pili nedeniyle öldüğünde ebeveyne uyarı bildirimi gösterir.
     */
    fun showServiceDisabledNotification(context: Context) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = context.getString(R.string.notification_service_disabled_title)
        val content = context.getString(R.string.notification_service_disabled_desc)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(content)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (!PermissionChecker.hasNotificationPermission(context)) {
            return
        }

        try {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID_SERVICE_STOPPED, notification)
        } catch (_: SecurityException) {
        } catch (_: Exception) {}
    }
}
