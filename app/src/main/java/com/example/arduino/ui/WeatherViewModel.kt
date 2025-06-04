package com.example.arduino.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arduino.network.WeatherApi
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.arduino.data.AppDatabase
import com.example.arduino.data.WeatherDao
import com.example.arduino.data.WeatherEntity

class WeatherViewModel (application: Application): ViewModel() {
    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "arduino_database"
    ).build()

    private val weatherDao = db.weatherDao()

    var uiState: WeatherUiState by mutableStateOf(WeatherUiState.Loading)
        private set

    init {
        getWeather()
    }

    fun getWeather() {
        viewModelScope.launch {
            uiState = WeatherUiState.Loading
            try {
                val response = WeatherApi.retrofitService.getCurrentWeather(
                    latitude = 43.85,
                    longitude = 18.36
                )

                val weather = response.current_weather
                val hourly = response.hourly

                if (weather == null || hourly == null) {
                    uiState = WeatherUiState.Error
                    return@launch
                }

                val index = hourly.time.indexOf(weather.time)
                val humidity = if (index != -1) hourly.relative_humidity_2m[index] else 0f
                val precipitation = if (index != -1) hourly.precipitation_probability[index] else 0f

                if (weather != null) {
                    val entity = WeatherEntity(
                        temperature = weather.temperature,
                        wind = weather.windspeed,
                        humidity = humidity.toFloat(),
                        precipitation = precipitation.toFloat(),
                        weatherCode = weather.weathercode,
                        time = weather.time
                    )
                    weatherDao.insertWeather(entity)

                    uiState = WeatherUiState.Success(
                        temperature = entity.temperature,
                        wind = entity.wind,
                        humidity = entity.humidity,
                        precipitation = entity.precipitation,
                        weatherCode = entity.weatherCode,
                        time = entity.time
                    )
                }
                else {
                    loadFromCache()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = WeatherUiState.Error
            }
        }
    }

    private suspend fun loadFromCache() {
        val cached = weatherDao.getWeather()
        if (cached != null) {
            uiState = WeatherUiState.Success(
                temperature = cached.temperature,
                wind = cached.wind,
                humidity = cached.humidity,
                precipitation = cached.precipitation,
                weatherCode = cached.weatherCode,
                time = cached.time
            )
        } else {
            uiState = WeatherUiState.Error
        }
    }
}

