package com.Lkmobile.service

import android.app.*
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.Lkmobile.R
import com.Lkmobile.MainActivity
import com.Lkmobile.util.AppLogger
import com.Lkmobile.util.GameDetector
import com.Lkmobile.service.LkAccessibilityService
import java.util.*

class MonitoringService : Service() {
    private var timer: Timer? = null
    private val NOTIFICATION_ID = 101
    private val CHANNEL_ID = "monitoring_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        AppLogger.i("Foreground Service Created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMonitoring()
        return START_STICKY
    }

    private fun startMonitoring() {
        timer?.cancel()
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val isAccessibilityRunning = LkAccessibilityService.isGameInForeground()
                val isLegacyRunning = GameDetector.isGameRunning(this@MonitoringService)
                
                // Priority: Accessibility Service (Instant) -> Legacy Detector (Polling fallback)
                val isGameRunning = isAccessibilityRunning || isLegacyRunning
                
                if (isGameRunning) {
                    val method = if (isAccessibilityRunning) "Accessibility" else "Legacy Fallback"
                    AppLogger.d("MLBB detected running via $method")
                }
                
                // Update status on server if needed
                updatePlayerStatus(isGameRunning)
            }
        }, 0, 3000)
    }

    private fun updatePlayerStatus(isPlaying: Boolean) {
        // This will be implemented to sync with MainViewModel/Repository
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Monitoring Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MLBB Launcher Running")
            .setContentText("Monitoring game state...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        AppLogger.w("Foreground Service Destroyed")
        timer?.cancel()
        super.onDestroy()
    }
}
