package com.example.arduino.ui

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arduino.R

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 64.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 70.dp
            )
            .verticalScroll(rememberScrollState()),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LivingRoom(viewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LivingRoom(viewModel: MainViewModel = viewModel()){
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            val success = viewModel.connectToArduino()
            Toast.makeText(
                context,
                if (success) "Povezano!" else "Greška pri povezivanju!",
                Toast.LENGTH_SHORT
            ).show()
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            WeatherScreen(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.connected_devices)
            )
            NumberBadge(4)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth()
        ){
            Devices(stringResource(R.string.lamp), stringResource(R.string.color), "White", viewModel, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            Devices(stringResource(R.string.tv), stringResource(R.string.channel), settings.channel, viewModel, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth()
        ){
            Devices(stringResource(R.string.air), stringResource(R.string.temperature), "${settings.temperatureThreshold}°C", viewModel, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            Devices(stringResource(R.string.speaker), stringResource(R.string.volume), "${settings.buzzerVolume.toInt()}%", viewModel, Modifier.weight(1f))
        }
    }
}

@Composable
fun ToggleSwitchWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Color(0xFF2196F3),
            )
        )

        Text(
            text = if (checked) "On" else "Off",
            style = typography.bodySmall,
            color = Color.Gray
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Devices(
    deviceName: String,
    deviceLabel: String,
    deviceStatus: String,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val deviceState by when (deviceName) {
        "Lamp" -> viewModel.lampSwitchState.collectAsState()
        "Smart TV" -> viewModel.tvSwitchState.collectAsState()
        "Air Conditioner" -> viewModel.acSwitchState.collectAsState()
        "Speaker" -> viewModel.speakerSwitchState.collectAsState()
        else -> remember { mutableStateOf(false) }
    }

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Icon(
                    imageVector = viewModel.getDeviceIcon(deviceName),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp),
                    tint = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = deviceLabel,
                        style = typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = deviceStatus,
                        style = typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = deviceName,
                style = typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            ToggleSwitchWithLabel(
                checked = deviceState,
                onCheckedChange = { isChecked ->
                    viewModel.onDeviceSwitchToggled(deviceName, isChecked)
                }
            )   
        }
    }
}

@Composable
fun NumberBadge(number: Int) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFF2196F3), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
