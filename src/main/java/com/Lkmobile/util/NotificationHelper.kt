package com.Lkmobile.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.Lkmobile.R

object NotificationHelper {

    private const val CHANNEL_ID = "game_invites_v2"
    private const val STATUS_CHANNEL_ID = "game_status_v1"
    private const val NOTIFICATION_ID_BASE = 1000
    private const val STATUS_NOTIFICATION_ID = 999

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Game Invites"
            val descriptionText = "Notifications for game invitations from other players"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                enableLights(true)
                lightColor = Color.MAGENTA
            }

            val statusName = "Game Status"
            val statusDesc = "Shows your current game status in notifications"
            val statusImportance = NotificationManager.IMPORTANCE_LOW
            val statusChannel = NotificationChannel(STATUS_CHANNEL_ID, statusName, statusImportance).apply {
                description = statusDesc
                setShowBadge(false)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(statusChannel)
        }
    }

    fun updateStatusNotification(context: Context, isPlaying: Boolean) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val statusText = if (isPlaying) "Status: In Game (Mobile Legends)" else "Status: Online"
        val icon = if (isPlaying) android.R.drawable.ic_media_play else android.R.drawable.ic_dialog_info

        val intent = Intent(context, Class.forName("com.Lkmobile.MainActivity")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, STATUS_CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle("LK Mobile")
            .setContentText(statusText)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        with(NotificationManagerCompat.from(context)) {
            notify(STATUS_NOTIFICATION_ID, builder.build())
        }
    }

    fun showInviteNotification(context: Context, fromUserName: String, inviteId: Int = 0) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val gameIntent = context.packageManager.getLaunchIntentForPackage(
            GameDetector.getGamePackageName()
        ) ?: Intent(context, Class.forName("com.Lkmobile.MainActivity"))

        gameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            context,
            inviteId,
            gameIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New Game Invite!")
            .setContentText("$fromUserName invited you to play Mobile Legends!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setLights(Color.MAGENTA, 3000, 3000)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setFullScreenIntent(pendingIntent, true) // High priority pop-up

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID_BASE + inviteId, builder.build())
        }
    }

    fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
