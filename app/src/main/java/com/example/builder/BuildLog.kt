package com.example.builder

enum class LogLevel {
    INFO,
    DEBUG,
    COMPILER,
    SUCCESS,
    WARNING,
    ERROR
}

data class BuildLogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val message: String,
    val level: LogLevel = LogLevel.INFO,
    val stepIndex: Int = 0,
    val progress: Float = 0f
)
