package com.Lkmobile.util

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

object GameDetector {

    private val MOBILE_LEGENDS_PACKAGES = listOf(
        "com.mobile.legends",
        "com.mobile.legends.usa",
        "com.moonton.mobilelegends",
        "com.vng.mlbbvn",
        "com.libii.mlbb",
        "com.mobilelegends.mi"
    )

    fun isGameInstalled(context: Context): Boolean {
        return MOBILE_LEGENDS_PACKAGES.any { isPackageInstalled(context, it) }
    }

    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, 0)
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun isGameRunning(context: Context): Boolean {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            
            // Priority 1: Check Running App Processes (Most reliable on modern Android with foreground importance)
            val runningApps = activityManager.runningAppProcesses
            val isRunningInProcesses = runningApps?.any { process ->
                MOBILE_LEGENDS_PACKAGES.contains(process.processName) &&
                (process.importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            } ?: false
            
            if (isRunningInProcesses) return true

            // Priority 2: UsageStats (as fallback)
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 30000 // Narrower window for better accuracy

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val stats = usageStatsManager?.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime)
                val isGameActive = stats?.any { stat -> 
                    MOBILE_LEGENDS_PACKAGES.contains(stat.packageName) && 
                    (endTime - stat.lastTimeUsed) < 15000 // Very recent usage
                } ?: false
                
                if (isGameActive) return true
            }

            // Priority 3: ActivityManager.getRunningTasks (Deprecated but still works for some foreground detection)
            @Suppress("DEPRECATION")
            val tasks = activityManager.getRunningTasks(1)
            if (tasks.isNotEmpty()) {
                val topActivity = tasks[0].topActivity?.packageName
                if (MOBILE_LEGENDS_PACKAGES.contains(topActivity)) return true
            }

            false
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
                @Suppress("DEPRECATION")
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

    fun launchGame(context: Context): Boolean {
        for (pkg in MOBILE_LEGENDS_PACKAGES) {
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
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mobile.legends")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(marketIntent)
        } catch (e: Exception) {}
        return false
    }

    fun getGamePackageName(): String = "com.mobile.legends"
}
