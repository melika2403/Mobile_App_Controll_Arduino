package com.example.arduino.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arduino.data.DeviceSettings
import kotlin.math.*
import androidx.compose.ui.input.pointer.*
import com.example.arduino.R
import androidx.compose.ui.res.stringResource

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

    LazyColumn(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Tv,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp),
                    tint = Color(0xFF2196F3)
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.tv_settings), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }

        item { Spacer(Modifier.height(2.dp)) }

        item {
            Column {
                Text(stringResource(R.string.channel), style = MaterialTheme.typography.labelMedium)
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedChannel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.select)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedChannel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.select)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color(0xFF0077FF), // Underline when focused
                                unfocusedIndicatorColor = Color(0xFF0077FF).copy(alpha = 0.5f),
                                focusedLabelColor = Color(0xFF0077FF),
                                unfocusedLabelColor = Color(0xFF0077FF).copy(alpha = 0.6f),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = Color(0xFF0077FF)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            channels.forEach { channel ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            channel,
                                            color = if (selectedChannel == channel) {
                                                Color(0xFF0077FF)
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    },
                                    onClick = {
                                        selectedChannel = channel
                                        expanded = false
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = MaterialTheme.colorScheme.onSurface,
                                        leadingIconColor = Color(0xFF0077FF),
                                        trailingIconColor = Color(0xFF0077FF),
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Divider() }
        item { Spacer(Modifier.height(2.dp)) }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Speaker,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp),
                    tint = Color(0xFF2196F3)
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.speaker_settings), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }

        item { Spacer(Modifier.height(2.dp)) }

        item {
            Column {
                Text("Buzzer Volume: ${buzzerVolume.toInt()}%", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = buzzerVolume,
                    onValueChange = { buzzerVolume = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF0077FF),
                        activeTrackColor = Color(0xFF0077FF),
                        inactiveTrackColor = Color(0xFF0077FF).copy(alpha = 0.24f)
                    )
                )
            }
        }

        item { Divider() }
        item { Spacer(Modifier.height(2.dp)) }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AcUnit,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp),
                    tint = Color(0xFF2196F3)
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.ac_settings), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }

        item { Spacer(Modifier.height(2.dp)) }

        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Auto AC", style = MaterialTheme.typography.labelMedium)
                    Switch(
                        checked = autoACEnabled,
                        onCheckedChange = { autoACEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFF2196F3),
                        )
                    )
                }

                if (autoACEnabled) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Turn AC on above: ${temperatureThreshold.toInt()}°C",
                        style = MaterialTheme.typography.labelMedium
                    )

                    ThermostatDial(
                        value = temperatureThreshold,
                        onValueChange = { temperatureThreshold = it },
                        min = 20f,
                        max = 40f
                    )
                }
            }
        }

        item { Divider() }
        item { Spacer(Modifier.height(2.dp)) }

        item {
            Button(
                onClick = {
                    val newSettings = DeviceSettings(
                        channel = selectedChannel,
                        buzzerVolume = buzzerVolume,
                        autoACEnabled = autoACEnabled,
                        temperatureThreshold = temperatureThreshold
                    )
                    onSettingsChange(newSettings)
                    mainViewModel.updateSettings(newSettings)
                    mainViewModel.sendAllSettingsToArduino()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0077FF),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Tune, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.save))
            }
        }

        item { Spacer(Modifier.height(36.dp)) }
    }
}


@Composable
fun ThermostatDial(
    value: Float,
    onValueChange: (Float) -> Unit,
    min: Float = 20f,
    max: Float = 40f,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(220.dp)
) {
    var angle by remember { mutableStateOf(((value - min) / (max - min)) * 180f) }

    val gestureModifier = modifier.pointerInput(Unit) {
        detectTapAndDrag(
            centerOffset = { size -> Offset(size.width / 2f, size.height.toFloat()) },
            radius = { size -> min(size.width, size.height) / 2.2f },
            onAngleChange = {
                // Limit angle between 0..180
                angle = it.coerceIn(0f, 180f)
                val newValue = min + (angle / 180f) * (max - min)
                onValueChange(newValue)
            }
        )
    }

    Canvas(modifier = gestureModifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = min(canvasWidth, canvasHeight) / 2.2f
        val center = Offset(x = canvasWidth / 2f, y = canvasHeight)

        // Background arc
        drawArc(
            color = Color.LightGray,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = 20f, cap = StrokeCap.Round)
        )

        // Foreground arc (progress)
        drawArc(
            color = Color(0xFF0077FF),
            startAngle = 180f,
            sweepAngle = angle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = 20f, cap = StrokeCap.Round)
        )

        // Knob
        val angleRad = Math.toRadians((180 + angle).toDouble())
        val knobX = center.x + cos(angleRad).toFloat() * radius
        val knobY = center.y + sin(angleRad).toFloat() * radius

        drawCircle(
            color = Color(0xFF0077FF),
            radius = 12f,
            center = Offset(knobX, knobY)
        )
    }
}

suspend fun PointerInputScope.detectTapAndDrag(
    centerOffset: (IntSize) -> Offset,
    radius: (IntSize) -> Float,
    onAngleChange: (Float) -> Unit
) {
    awaitPointerEventScope {
        while (true) {
            val down = awaitFirstDown(requireUnconsumed = false)
            val size = this@detectTapAndDrag.size
            val center = centerOffset(size)
            val r = radius(size)

            var pointerId = down.id
            val drag = awaitTouchSlopOrCancellation(down.id) { change, _ ->
                change.consumePositionChange()
            }

            if (drag != null) {
                // We're dragging
                while (true) {
                    val event = awaitPointerEvent()
                    val dragChange = event.changes.firstOrNull { it.id == pointerId }
                    if (dragChange != null && dragChange.pressed) {
                        dragChange.consumePositionChange()
                        val position = dragChange.position
                        val dx = position.x - center.x
                        val dy = position.y - center.y

                        var angle = atan2(dy, dx) * (180f / PI.toFloat()) - 180f
                        angle = (angle + 360f) % 360f
                        if (angle in 0f..180f) {
                            onAngleChange(angle)
                        }
                    } else {
                        break
                    }
                }
            }
        }
    }
}
