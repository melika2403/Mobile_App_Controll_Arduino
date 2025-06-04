package com.example.arduino.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val current_weather: CurrentWeather? = null,
    val hourly: HourlyWeather? = null
)

@Serializable
data class CurrentWeather(
    val temperature: Float,
    val windspeed: Float,
    val winddirection: Float,
    val weathercode: Int,
    val time: String
)

@Serializable
data class HourlyWeather(
    val time: List<String>,
    val relative_humidity_2m: List<Float>,
    val precipitation_probability: List<Float>
)

