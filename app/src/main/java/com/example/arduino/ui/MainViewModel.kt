package com.example.arduino.ui

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.arduino.data.AppDatabase
import com.example.arduino.data.ArduinoRepository
import com.example.arduino.data.DeviceUsageLog
import com.example.arduino.data.DeviceUsageLogDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration


class MainViewModel(application: Application) :  AndroidViewModel(application) {
    private val repo = ArduinoRepository(application)

    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "arduino_database"
    ).build()

    private val deviceUsageLogDao = db.deviceUsageLogDao()

    fun connectToArduino(): Boolean {
        val success = repo.connect()
        return success
    }

    private val _ledStatus = MutableStateFlow(false)
    val ledStatus: StateFlow<Boolean> get() = _ledStatus

    private val _tvStatus = MutableStateFlow(false)
    val tvStatus: StateFlow<Boolean> get() = _tvStatus

    private val _acStatus = MutableStateFlow(false)
    val acStatus: StateFlow<Boolean> get() = _acStatus

    private val _speakerStatus = MutableStateFlow(false)
    val speakerStatus: StateFlow<Boolean> get() = _speakerStatus

    init {
        repo.onArduinoDataReceived = { data ->
            updateDeviceStatus(data)
        }
    }

    fun updateDeviceStatus(data: String) {
        Log.d("MainViewModel", "Updating device status with: $data")
        when {
            data.contains("LED ON", ignoreCase = true) -> _ledStatus.value = true
            data.contains("LED OFF", ignoreCase = true) -> _ledStatus.value = false
            data.contains("TV ON", ignoreCase = true) -> _tvStatus.value = true
            data.contains("TV OFF", ignoreCase = true) -> _tvStatus.value = false
            data.contains("AC ON", ignoreCase = true) -> _acStatus.value = true
            data.contains("AC OFF", ignoreCase = true) -> _acStatus.value = false
            data.contains("SPEAKER ON", ignoreCase = true) -> _speakerStatus.value = true
            data.contains("SPEAKER OFF", ignoreCase = true) -> _speakerStatus.value = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun turnOnLed() {
        logDeviceTurnedOn("Lamp")
        repo.sendLEDOn()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun turnOffLed() {
        logDeviceTurnedOff("Lamp")
        repo.sendLEDOff()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun turnOnTV() {
        logDeviceTurnedOn("Smart TV")
        repo.sendTVOn()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun turnOffTV() {
        logDeviceTurnedOff("Smart TV")
        repo.sendTVOff()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun turnOnAC() {
        logDeviceTurnedOn("Air Conditioner")
        repo.sendACOn()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun turnOffAC() {
        logDeviceTurnedOff("Air Conditioner")
        repo.sendACOff()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun turnOnSpeaker() {
        logDeviceTurnedOn("Speaker")
        repo.sendSpeakerOn()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun turnOffSpeaker() {
        logDeviceTurnedOff("Speaker")
        repo.sendSpeakerOff()
    }


    fun disconnect() = repo.disconnect()

    @RequiresApi(Build.VERSION_CODES.O)
    fun logDeviceTurnedOn(deviceName: String) {
        viewModelScope.launch {
            val activeLog = deviceUsageLogDao.getActiveLog(deviceName)
            if (activeLog == null) {
                val newLog = DeviceUsageLog(
                    deviceName = deviceName,
                    startTime = LocalDateTime.now()
                )
                deviceUsageLogDao.insertLog(newLog)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun logDeviceTurnedOff(deviceName: String) {
        viewModelScope.launch {
            val activeLog = deviceUsageLogDao.getActiveLog(deviceName)
            if (activeLog != null) {
                val updatedLog = activeLog.copy(endTime = LocalDateTime.now())
                deviceUsageLogDao.updateLog(updatedLog)
            }
        }
    }
}