package com.example.arduino.ui

import android.os.Build
import android.view.ViewGroup
import android.graphics.Color as AndroidColor
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arduino.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeviceUsageScreen(viewModel: DeviceUsageViewModel = viewModel()) {
    val deviceNames = viewModel.deviceNames.collectAsState()
    val selectedDevice = viewModel.selectedDevice.collectAsState()
    val logsThisWeek = viewModel.logsThisWeek.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDeviceNames()
    }

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(
        top = 64.dp,
        start = 16.dp,
        end = 16.dp,
        bottom = 100.dp
    )) {
        Text(stringResource(R.string.device_usage), style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (selectedDevice.value == null) {
            deviceNames.value.forEachIndexed { index, name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectDevice(name) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = viewModel.getDeviceIcon(name),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(4.dp),
                        tint = Color(0xFF2196F3)
                    )
                    Text(
                        text = name,
                        modifier = Modifier.padding(4.dp)
                    )
                }

                if (index < deviceNames.value.lastIndex) {
                    Divider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            TextButton(
                onClick = { viewModel.selectDevice(null) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF2196F3)
                )
            ) {
                Text(stringResource(R.string.back_to_device_list), style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Usage for: ${selectedDevice.value}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            val weeklyUsage: Map<DayOfWeek, Float> = logsThisWeek.value
                .groupBy { it.startTime.toLocalDate().dayOfWeek }
                .mapValues { (_, logsForDay) ->
                    logsForDay.sumOf {
                        val endTime = it.endTime ?: LocalDateTime.now()
                        Duration.between(it.startTime, endTime).toMinutes().toDouble()
                    }.toFloat()
                }

            val days = DayOfWeek.values()
            val usageByDay = days.mapIndexed { index, day ->
                val minutes = weeklyUsage[day] ?: 0f
                index.toFloat() to minutes
            }

            val dayLabels = days.mapIndexed { index, day ->
                index.toFloat() to day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            }.toMap()

            val barEntries = usageByDay.map { BarEntry(it.first, it.second) }

            AndroidView(
                factory = { context ->
                    BarChart(context).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600)

                        val dataSet = BarDataSet(barEntries, "Minutes Used").apply {
                            color = AndroidColor.rgb(33, 150, 243)
                            valueTextColor = AndroidColor.rgb(33, 150, 243)
                            valueTextSize = 12f
                        }

                        data = BarData(dataSet)
                        description.isEnabled = false
                        setFitBars(true)
                        animateY(1000)

                        axisLeft.axisMinimum = 0f
                        axisLeft.textColor = AndroidColor.rgb(33, 150, 243)
                        axisRight.isEnabled = false
                        xAxis.setDrawGridLines(false)
                        axisLeft.setDrawGridLines(false)
                        axisRight.setDrawGridLines(false)
                        xAxis.setDrawAxisLine(false)
                        axisLeft.setDrawAxisLine(false)
                        axisRight.setDrawAxisLine(false)
                        axisRight.isEnabled = false

                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(false)
                            granularity = 1f
                            valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                                dayLabels.map { it.value }
                            )
                            textColor = AndroidColor.DKGRAY
                            textSize = 12f
                        }

                        legend.isEnabled = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}