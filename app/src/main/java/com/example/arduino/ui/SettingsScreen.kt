package com.example.arduino.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arduino.data.DeviceSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    channels: List<String>,
    currentSettings: DeviceSettings,
    onSettingsChange: (DeviceSettings) -> Unit,
    onSubmit: () -> Unit
) {
    val mainViewModel: MainViewModel = viewModel()

    var selectedChannel by remember { mutableStateOf(currentSettings.channel) }
    var buzzerVolume by remember { mutableFloatStateOf(currentSettings.buzzerVolume) }
    var autoACEnabled by remember { mutableStateOf(currentSettings.autoACEnabled) }
    var temperatureThreshold by remember { mutableFloatStateOf(currentSettings.temperatureThreshold) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        // Channel dropdown
        Text("Channel")
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedChannel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Channel") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                channels.forEach { channel ->
                    DropdownMenuItem(
                        text = { Text(channel) },
                        onClick = {
                            selectedChannel = channel
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Buzzer volume slider
        Text("Buzzer Volume: ${buzzerVolume.toInt()}")
        Slider(
            value = buzzerVolume,
            onValueChange = { buzzerVolume = it },
            valueRange = 0f..100f
        )

        Spacer(Modifier.height(16.dp))

        // Auto AC toggle and temperature
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Auto AC")
            Switch(
                checked = autoACEnabled,
                onCheckedChange = { autoACEnabled = it }
            )
        }

        if (autoACEnabled) {
            Spacer(Modifier.height(8.dp))
            Text("Turn AC on above °C: ${temperatureThreshold.toInt()}")
            Slider(
                value = temperatureThreshold,
                onValueChange = { temperatureThreshold = it },
                valueRange = 20f..40f
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            val newSettings = DeviceSettings(
                channel = selectedChannel,
                buzzerVolume = buzzerVolume,
                autoACEnabled = autoACEnabled,
                temperatureThreshold = temperatureThreshold
            )
            onSettingsChange(newSettings)
            mainViewModel.sendAllSettingsToArduino()
        }) {
            Text("Save & Send to Arduino")
        }
    }
}
