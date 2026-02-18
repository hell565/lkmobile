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
    private const val MOBILE_LEGENDS_USA_PACKAGE = "com.mobile.legends.usa"

    fun isGameInstalled(context: Context): Boolean {
        return isPackageInstalled(context, MOBILE_LEGENDS_PACKAGE) || 
               isPackageInstalled(context, MOBILE_LEGENDS_USA_PACKAGE)
    }

    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isGameRunning(context: Context): Boolean {
        return try {
            val packages = listOf(MOBILE_LEGENDS_PACKAGE, MOBILE_LEGENDS_USA_PACKAGE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                val endTime = System.currentTimeMillis()
                val beginTime = endTime - 15000 
                val stats = usageStatsManager?.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime)
                
                if (stats.isNullOrEmpty()) {
                    return packages.any { isGameRunningFallback(context, it) }
                }

                val isGameActive = stats.any { stat -> 
                    packages.contains(stat.packageName) && stat.lastTimeUsed > (endTime - 12000)
                }
                
                if (!isGameActive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val events = usageStatsManager.queryEvents(beginTime, endTime)
                    val event = android.app.usage.UsageEvents.Event()
                    while (events.hasNextEvent()) {
                        events.getNextEvent(event)
                        if (packages.contains(event.packageName) && 
                            (event.eventType == android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED ||
                             event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND)) {
                            return true
                        }
                    }
                }
                isGameActive
            } else {
                packages.any { isGameRunningFallback(context, it) }
            }
        } catch (e: Exception) {
            false
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

    private fun isGameRunningFallback(context: Context, packageName: String): Boolean {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = activityManager.runningAppProcesses ?: return false
            runningApps.any {
                it.processName == packageName &&
                        (it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                         it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
            }
        } catch (e: Exception) {
            false
        }
    }

    fun launchGame(context: Context): Boolean {
        val packages = listOf(MOBILE_LEGENDS_PACKAGE, MOBILE_LEGENDS_USA_PACKAGE)
        for (pkg in packages) {
            try {
                val intent = context.packageManager.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                    return true
                }
            } catch (e: Exception) {}
        }
        
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$MOBILE_LEGENDS_PACKAGE")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(marketIntent)
        } catch (e: Exception) {}
        return false
    }

    fun getGamePackageName(): String = MOBILE_LEGENDS_PACKAGE
}
