package com.example.arduino.ui

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
import kotlinx.coroutines.launch

class DeviceUsageViewModel(private val dao: DeviceUsageLogDao) : ViewModel() {

    private val _deviceNames = MutableStateFlow<List<String>>(emptyList())
    val deviceNames: StateFlow<List<String>> = _deviceNames

    private val _selectedDevice = MutableStateFlow<String?>(null)
    val selectedDevice: StateFlow<String?> = _selectedDevice

    private val _logs = MutableStateFlow<List<DeviceUsageLog>>(emptyList())
    val logs: StateFlow<List<DeviceUsageLog>> = _logs

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
            _logs.value = dao.getLogsForDevice(name)
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
