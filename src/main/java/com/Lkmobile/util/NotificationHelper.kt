package com.Lkmobile.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.Lkmobile.R

object NotificationHelper {

    private const val CHANNEL_ID = "game_invites"
    private const val NOTIFICATION_ID_BASE = 1000

    fun createNotificationChannel(context: Context) {
        val name = context.getString(R.string.invite_channel_name)
        val descriptionText = context.getString(R.string.invite_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableVibration(true)
            enableLights(true)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showInviteNotification(context: Context, fromUserName: String, inviteId: Int = 0) {
        val gameIntent = context.packageManager.getLaunchIntentForPackage(
            GameDetector.getGamePackageName()
        )

        val pendingIntent = if (gameIntent != null) {
            gameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            PendingIntent.getActivity(
                context,
                inviteId,
                gameIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            val fallbackIntent = Intent(context, Class.forName("com.Lkmobile.MainActivity"))
            PendingIntent.getActivity(
                context,
                inviteId,
                fallbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(context.getString(R.string.invite_notification_title))
            .setContentText(
                context.getString(R.string.invite_notification_text, fromUserName)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID_BASE + inviteId, notification)
        } catch (e: SecurityException) {
            // Notification permission not granted
        }
    }

    fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
