package com.example.arduino.ui

sealed interface WeatherUiState {
    data class Success(
        val temperature: Float,
        val wind: Float,
        val humidity: Float,
        val precipitation: Float,
        val weatherCode: Int,
        val time: String
    ) : WeatherUiState

    object Error : WeatherUiState
    object Loading : WeatherUiState
}
