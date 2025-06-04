package com.example.arduino.ui

import android.app.Application
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val weatherViewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(context.applicationContext as Application)
    )

    val state = weatherViewModel.uiState

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        when (state) {
            is WeatherUiState.Loading -> CircularProgressIndicator()
            is WeatherUiState.Success -> WeatherCard(
                temperature = state.temperature,
                wind = state.wind,
                humidity = state.humidity,
                precipitation = state.precipitation,
                weatherCode = state.weatherCode,
                date = state.time.take(10)
            )
            is WeatherUiState.Error -> Text("Failed to load weather.")
        }
    }
}

@Composable
fun WeatherCard(
    temperature: Float,
    wind: Float,
    humidity: Float,
    precipitation: Float,
    weatherCode: Int,
    date: String
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(date, color = Color.Gray, fontSize = 14.sp)
                    Text(getWeatherDescription(weatherCode), color = Color.White, fontSize = 22.sp)
                    Text("Limited Sunshine", color = Color.LightGray, fontSize = 14.sp)
                }
                Text(
                    text = "${temperature.toInt()}°C",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(color = Color.Gray.copy(alpha = 0.4f))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                WeatherInfoItem(icon = Icons.Default.InvertColors, label = "Humidity", value = "$humidity%")
                WeatherInfoItem(icon = Icons.Default.Air, label = "Wind", value = "${wind.toInt()} km/h")
                WeatherInfoItem(icon = Icons.Default.Umbrella, label = "Precip", value = "$precipitation%")
            }
        }
    }
}

@Composable
fun WeatherInfoItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Color.White)
        Text(label, color = Color.LightGray, fontSize = 12.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

fun getWeatherDescription(code: Int): String {
    return when (code) {
        0 -> "Clear"
        1, 2 -> "Partly Cloudy"
        3 -> "Cloudy"
        in 45..48 -> "Fog"
        in 51..67 -> "Drizzle"
        in 71..77 -> "Snow"
        in 80..82 -> "Showers"
        in 95..99 -> "Thunderstorm"
        else -> "Unknown"
    }
}

