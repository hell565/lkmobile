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
                
                // Check if permission is granted
                val endTime = System.currentTimeMillis()
                val beginTime = endTime - 10000
                val stats = usageStatsManager?.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime)
                
                if (stats.isNullOrEmpty()) {
                    // Try to check if we can even access usage stats
                    return isGameRunningFallback(context)
                }

                val isGameActive = stats.any { it.packageName == MOBILE_LEGENDS_PACKAGE && it.lastTimeUsed > beginTime }
                
                if (!isGameActive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val events = usageStatsManager.queryEvents(beginTime, endTime)
                    val event = android.app.usage.UsageEvents.Event()
                    while (events.hasNextEvent()) {
                        events.getNextEvent(event)
                        if (event.packageName == MOBILE_LEGENDS_PACKAGE && 
                            (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED ||
                             event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND)) {
                            return true
                        }
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

    fun hasUsageStatsPermission(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(android.app.AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
            } else {
                appOps.checkOpNoThrow(android.app.AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
            }
            mode == android.app.AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    fun requestUsageStatsPermission(context: Context) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
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
