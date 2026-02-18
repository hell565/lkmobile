package com.Lkmobile

import android.app.Application
import com.Lkmobile.util.NotificationHelper

class LkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
