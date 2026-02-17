package com.Lkmobile.util

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
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
            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                    ?: return false

            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 5000

            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                beginTime,
                endTime
            )

            if (stats.isNullOrEmpty()) {
                return isGameRunningFallback(context)
            }

            stats.any { it.packageName == MOBILE_LEGENDS_PACKAGE && it.lastTimeUsed > beginTime }
        } catch (e: Exception) {
            isGameRunningFallback(context)
        }
    }

    private fun isGameRunningFallback(context: Context): Boolean {
        return try {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = activityManager.runningAppProcesses ?: return false
            runningApps.any {
                it.processName == MOBILE_LEGENDS_PACKAGE &&
                        it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        } catch (e: Exception) {
            false
        }
    }

    fun launchGame(context: Context): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(MOBILE_LEGENDS_PACKAGE)
            if (intent != null) {
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getGamePackageName(): String = MOBILE_LEGENDS_PACKAGE
}
