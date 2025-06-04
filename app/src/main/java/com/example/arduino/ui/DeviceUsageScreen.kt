package com.example.arduino.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.core.entry.entriesOf
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.Duration
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeviceUsageScreen(viewModel: DeviceUsageViewModel = viewModel()) {
    val deviceNames = viewModel.deviceNames.collectAsState()
    val selectedDevice = viewModel.selectedDevice.collectAsState()
    val logs = viewModel.logs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDeviceNames()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Device Usage", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (selectedDevice.value == null) {
            deviceNames.value.forEach { name ->
                Button(
                    onClick = { viewModel.selectDevice(name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(name)
                }
            }
        } else {
            Button(
                onClick = { viewModel.selectDevice("") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Back to Device List")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Usage for: ${selectedDevice.value}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            val weeklyUsage: Map<DayOfWeek, Float> = logs.value
                .groupBy { log ->
                    log.startTime.toLocalDate().dayOfWeek
                }
                .mapValues { (_, logsForDay) ->
                    logsForDay.sumOf { log ->
                        val endTime = log.endTime ?: LocalDateTime.now()
                        Duration.between(log.startTime, endTime).toMinutes().toDouble()
                    }.toFloat()
                }

            val days = DayOfWeek.values()

            val usageByDay = days.mapIndexed { index, day ->
                val minutes = weeklyUsage[day] ?: 0f
                index.toFloat() to minutes
            }

            val chartModel = entryModelOf(*usageByDay.toTypedArray())

            val dayLabels = days.mapIndexed { index, day ->
                index.toFloat() to day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            }.toMap()

            val bottomAxis = rememberBottomAxis(
                valueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                    dayLabels[value] ?: ""
                }
            )

            ProvideChartStyle {
                Chart(
                    chart = columnChart(),
                    model = chartModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    bottomAxis = bottomAxis
                )
            }
        }
    }
}