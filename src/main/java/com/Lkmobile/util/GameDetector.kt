package com.Lkmobile.util

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

object GameDetector {

    private const val MOBILE_LEGENDS_PACKAGE = "com.mobile.legends"

    fun isGameInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(MOBILE_LEGENDS_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isGameRunning(context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                    ?: return isGameRunningFallback(context)

                val endTime = System.currentTimeMillis()
                val beginTime = endTime - 10000 // 10 second window

                val stats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_BEST,
                    beginTime,
                    endTime
                )

                if (stats.isNullOrEmpty()) {
                    return isGameRunningFallback(context)
                }

                // Check for foreground activity in usage stats
                val isGameActive = stats.any { it.packageName == MOBILE_LEGENDS_PACKAGE && it.lastTimeUsed > beginTime }
                
                // Extra check for Honor/Huawei power management
                if (!isGameActive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    return usageStatsManager.queryEvents(beginTime, endTime).let { events ->
                        var found = false
                        val event = android.app.usage.UsageEvents.Event()
                        while (events.hasNextEvent()) {
                            events.getNextEvent(event)
                            if (event.packageName == MOBILE_LEGENDS_PACKAGE && 
                                (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED ||
                                 event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND)) {
                                found = true
                                break
                            }
                        }
                        found
                    }
                }
                isGameActive
            } else {
                isGameRunningFallback(context)
            }
        } catch (e: Exception) {
            isGameRunningFallback(context)
        }
    }

    private fun isGameRunningFallback(context: Context): Boolean {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            
            // Check running processes - importance check is more reliable for foreground
            val runningApps = activityManager.runningAppProcesses ?: return false
            runningApps.any {
                it.processName == MOBILE_LEGENDS_PACKAGE &&
                        (it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                         it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
            }
        } catch (e: Exception) {
            false
        }
    }

    fun launchGame(context: Context): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(MOBILE_LEGENDS_PACKAGE)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                // Use the context from which the method was called to start the activity
                context.startActivity(intent)
                true
            } else {
                val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$MOBILE_LEGENDS_PACKAGE")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(marketIntent)
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getGamePackageName(): String = MOBILE_LEGENDS_PACKAGE
}
