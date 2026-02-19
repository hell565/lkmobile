package com.Lkmobile.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.Lkmobile.util.AppLogger

class LkAccessibilityService : AccessibilityService() {

    companion object {
        var currentForegroundPackage: String? = null
        private val MOBILE_LEGENDS_PACKAGES = listOf(
            "com.mobile.legends",
            "com.mobile.legends.usa",
            "com.moonton.mobilelegends",
            "com.vng.mlbbvn",
            "com.libii.mlbb",
            "com.mobilelegends.mi"
        )

        fun isGameInForeground(): Boolean {
            return MOBILE_LEGENDS_PACKAGES.contains(currentForegroundPackage)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            if (packageName != null) {
                currentForegroundPackage = packageName
                if (MOBILE_LEGENDS_PACKAGES.contains(packageName)) {
                    AppLogger.d("Accessibility: MLBB detected in foreground: $packageName")
                } else {
                    // Log other apps occasionally to avoid flooding but show it's working
                    // AppLogger.d("Accessibility: Foreground app changed to: $packageName")
                }
            }
        }
    }

    override fun onInterrupt() {
        AppLogger.w("Accessibility Service Interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        AppLogger.i("Accessibility Service Connected")
    }
}
