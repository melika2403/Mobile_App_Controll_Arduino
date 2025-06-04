package com.example.arduino.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DeviceUsageLogDao {
    @Insert
    suspend fun insertLog(log: DeviceUsageLog): Long

    @Update
    suspend fun updateLog(log: DeviceUsageLog)

    @Query("SELECT * FROM device_usage_log WHERE deviceName = :deviceName")
    suspend fun getLogsForDevice(deviceName: String): List<DeviceUsageLog>

    @Query("SELECT * FROM device_usage_log WHERE deviceName = :deviceName AND endTime IS NULL LIMIT 1")
    suspend fun getActiveLog(deviceName: String): DeviceUsageLog?

    @Query("SELECT DISTINCT deviceName FROM device_usage_log")
    suspend fun getAllDeviceNames(): List<String>
}
