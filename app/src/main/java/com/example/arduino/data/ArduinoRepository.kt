package com.example.arduino.data

import android.content.Context
import android.util.Log
import com.example.arduino.ui.MainViewModel
import com.example.arduino.utils.UsbSerialManager

class ArduinoRepository(context: Context) {
    private val serialManager = UsbSerialManager(context)

    fun connect(): Boolean = serialManager.connect()

    var onArduinoDataReceived: ((String) -> Unit)? = null

    init {
        serialManager.onDataReceived = { data ->
            Log.d("ArduinoRepository", "Received data: $data")
            onArduinoDataReceived?.invoke(data)
        }
    }
    fun sendLEDOn() = serialManager.sendCommand("LED_ON")
    fun sendLEDOff() = serialManager.sendCommand("LED_OFF")

    fun sendTVOn() = serialManager.sendCommand("TV_ON")
    fun sendTVOff() = serialManager.sendCommand("TV_OFF")

    fun sendACOn() = serialManager.sendCommand("AC_ON")
    fun sendACOff() = serialManager.sendCommand("AC_OFF")

    fun sendSpeakerOn() = serialManager.sendCommand("SPEAKER_ON")
    fun sendSpeakerOff() = serialManager.sendCommand("SPEAKER_OFF")

    fun sendTVChannel(channel: String) {
        Log.d("ArduinoRepository", "sendCommand: TV_CHANNEL:$channel")
        serialManager.sendCommand("TV_CHANNEL:$channel")
    }

    fun sendBuzzerVolume(volume: Int) {
        serialManager.sendCommand("BUZZER_VOLUME:$volume")
    }

    fun sendAutoAC(enabled: Boolean, threshold: Float) {
        serialManager.sendCommand("AUTO_AC:${if (enabled) "ON" else "OFF"}:$threshold")
    }

    fun disconnect() = serialManager.close()
}