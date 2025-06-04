package com.example.arduino.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val id: Int = 0,
    val temperature: Float,
    val wind: Float,
    val humidity: Float,
    val precipitation: Float,
    val weatherCode: Int,
    val time: String
)
