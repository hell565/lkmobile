package com.Lkmobile.util

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppLogger {
    data class LogEntry(
        val timestamp: String,
        val level: String,
        val message: String
    )

    private val _logs = mutableStateListOf<LogEntry>()
    val logs: List<LogEntry> get() = _logs

    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun d(message: String) = addLog("DEBUG", message)
    fun i(message: String) = addLog("INFO", message)
    fun e(message: String) = addLog("ERROR", message)
    fun w(message: String) = addLog("WARN", message)

    private fun addLog(level: String, message: String) {
        val entry = LogEntry(
            timestamp = dateFormat.format(Date()),
            level = level,
            message = message
        )
        _logs.add(0, entry) // Add at top
        if (_logs.size > 2000) _logs.removeLast()
    }

    fun getLogsText(): String {
        return logs.joinToString("\n") { "[${it.timestamp}] ${it.level}: ${it.message}" }
    }

    fun clear() {
        _logs.clear()
    }
}
