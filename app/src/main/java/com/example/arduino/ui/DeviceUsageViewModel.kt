package com.example.arduino.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arduino.data.DeviceUsageLog
import com.example.arduino.data.DeviceUsageLogDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime

class DeviceUsageViewModel(private val dao: DeviceUsageLogDao) : ViewModel() {

    private val _deviceNames = MutableStateFlow<List<String>>(emptyList())
    val deviceNames: StateFlow<List<String>> = _deviceNames

    private val _selectedDevice = MutableStateFlow<String?>(null)
    val selectedDevice: StateFlow<String?> = _selectedDevice

    private val _logs = MutableStateFlow<List<DeviceUsageLog>>(emptyList())
    val logs: StateFlow<List<DeviceUsageLog>> = _logs

    @RequiresApi(Build.VERSION_CODES.O)
    val logsThisWeek = logs.map { allLogs ->
        val now = LocalDateTime.now()
        val startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay()
        allLogs.filter {
            it.startTime.isAfter(startOfWeek) || it.startTime.isEqual(startOfWeek)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadDeviceNames()
    }

    fun loadDeviceNames() {
        viewModelScope.launch {
            _deviceNames.value = dao.getAllDeviceNames()
        }
    }

    fun selectDevice(name: String?) {
        _selectedDevice.value = name
        if (name != null) loadLogsForDevice(name)
    }

    private fun loadLogsForDevice(name: String) {
        viewModelScope.launch {
            val logs = dao.getLogsForDevice(name)
            logs.forEach { Log.d("DeviceLog", "LOG: $it") }
            _logs.value = logs
        }
    }

    fun getDeviceIcon(deviceName: String): ImageVector {
        return when (deviceName) {
            "Lamp" -> Icons.Filled.Lightbulb
            "Smart TV" -> Icons.Filled.Tv
            "Air Conditioner" -> Icons.Filled.AcUnit
            "Speaker" -> Icons.Filled.Speaker
            else -> Icons.Filled.DeviceUnknown
        }
    }
}