package com.example.arduino.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arduino.data.DeviceUsageLogDao

class DeviceUsageViewModelFactory(
    private val dao: DeviceUsageLogDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceUsageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceUsageViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
