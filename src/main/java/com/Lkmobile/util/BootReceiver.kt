package com.Lkmobile.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.Lkmobile.service.MonitoringService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            AppLogger.i("Boot Completed - Restarting Service")
            val serviceIntent = Intent(context, MonitoringService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
