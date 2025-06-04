package com.example.arduino.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "device_usage_log")
data class DeviceUsageLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceName: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null
)
