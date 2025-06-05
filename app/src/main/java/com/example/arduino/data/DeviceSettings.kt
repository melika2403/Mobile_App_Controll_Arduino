package com.example.arduino.data

data class DeviceSettings(
    val channel: String,
    val buzzerVolume: Float,
    val autoACEnabled: Boolean,
    val temperatureThreshold: Float
)
