package com.example.arduino.ui

import android.app.Application
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.arduino.data.AppDatabase
import com.example.arduino.data.ArduinoRepository
import com.example.arduino.data.DeviceSettings
import com.example.arduino.data.DeviceUsageLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainViewModel(application: Application) :  AndroidViewModel(application) {
    private val repo = ArduinoRepository(application)

    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "arduino_database"
    ).build()

    private val deviceUsageLogDao = db.deviceUsageLogDao()

    private val _settings = MutableStateFlow(
        DeviceSettings(
            channel = "CH1",
            buzzerVolume = 50f,
            autoACEnabled = true,
            temperatureThreshold = 30f
        )
    )
    val settings = _settings.asStateFlow()

    fun updateSettings(newSettings: DeviceSettings) {
        _settings.value = newSettings

        viewModelScope.launch {
            try {
                sendAllSettingsToArduino()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Greška prilikom slanja ka Arduinu: ${e.message}", e)
            }
        }
    }


    val channels = listOf("CH1", "CH2", "CH3")

    fun sendAllSettingsToArduino() {
        val currentSettings = _settings.value

        try {
            repo.sendTVChannel(currentSettings.channel)
        } catch (e: Exception) {
            Log.e("sendSettings", "Greška pri slanju kanala: ${e.message}", e)
        }

        try {
            repo.sendBuzzerVolume(currentSettings.buzzerVolume.toInt())
        } catch (e: Exception) {
            Log.e("sendSettings", "Greška pri slanju volumena: ${e.message}", e)
        }

        try {
            repo.sendAutoAC(currentSettings.autoACEnabled, currentSettings.temperatureThreshold)
        } catch (e: Exception) {
            Log.e("sendSettings", "Greška pri slanju AC postavki: ${e.message}", e)
        }
    }


    private val _lampSwitchState = MutableStateFlow(false)
    val lampSwitchState: StateFlow<Boolean> = _lampSwitchState

    private val _tvSwitchState = MutableStateFlow(false)
    val tvSwitchState: StateFlow<Boolean> = _tvSwitchState

    private val _acSwitchState = MutableStateFlow(false)
    val acSwitchState: StateFlow<Boolean> = _acSwitchState

    private val _speakerSwitchState = MutableStateFlow(false)
    val speakerSwitchState: StateFlow<Boolean> = _speakerSwitchState

    fun setDeviceSwitchState(deviceName: String, isOn: Boolean) {
        when (deviceName) {
            "Lamp" -> _lampSwitchState.value = isOn
            "Smart TV" -> _tvSwitchState.value = isOn
            "Air Conditioner" -> _acSwitchState.value = isOn
            "Speaker" -> _speakerSwitchState.value = isOn
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDeviceSwitchToggled(deviceName: String, isChecked: Boolean) {
        setDeviceSwitchState(deviceName, isChecked)
        when (deviceName) {
            "Lamp" -> if (isChecked) turnOnLed() else turnOffLed()
            "Smart TV" -> if (isChecked) turnOnTV() else turnOffTV()
            "Air Conditioner" -> if (isChecked) turnOnAC() else turnOffAC()
            "Speaker" -> if (isChecked) turnOnSpeaker() else turnOffSpeaker()
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