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
                stats.any { it.packageName == MOBILE_LEGENDS_PACKAGE && it.lastTimeUsed > beginTime }
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
            // Priority 1: standard launch intent
            var intent = context.packageManager.getLaunchIntentForPackage(MOBILE_LEGENDS_PACKAGE)
            
            // Priority 2: Direct component launch for Honor/Huawei/Oppo if standard fails
            if (intent == null) {
                intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    setPackage(MOBILE_LEGENDS_PACKAGE)
                }
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or 
                               Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or
                               Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
                true
            } else {
                // Priority 3: Market fallback
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
