package com.example.arduino.ui

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arduino.R

@Composable
fun MainScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LivingRoom(viewModel)
    }
}

@Composable
fun Proba(viewModel: MainViewModel){
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val success = viewModel.connectToArduino()
            Toast.makeText(
                context,
                if (success) "Povezano!" else "Greška pri povezivanju!",
                Toast.LENGTH_SHORT
            ).show()
        }) {
            Text("Poveži se s Arduinom")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.turnOnLed() }) {
            Text("LED ON")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.turnOffLed() }) {
            Text("LED OFF")
        }
    }
}

@Composable
fun LivingRoom(viewModel: MainViewModel = viewModel()){
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
                text = "Connected Devices "
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
            Devices(stringResource(R.string.tv), stringResource(R.string.channel), "Nova BH", viewModel, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth()
        ){
            Devices(stringResource(R.string.air), stringResource(R.string.temperature), "20°C", viewModel, Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            Devices(stringResource(R.string.speaker), stringResource(R.string.volume), "80%", viewModel, Modifier.weight(1f))
        }
    }
}

@Composable
fun ToggleSwitchWithLabel(
    isCheckedInitial: Boolean = false,
    onTurnOn: () -> Unit,
    onTurnOff: () -> Unit
) {
    var isChecked by remember { mutableStateOf(isCheckedInitial) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                if (isChecked) {
                    onTurnOn()
                } else {
                    onTurnOff()
                }
            }
        )

        Text(
            text = if (isChecked) "On" else "Off",
            style = typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun Devices(
    deviceName: String,
    deviceLabel: String,
    deviceStatus: String,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val deviceState by when (deviceName) {
        "Lamp" -> viewModel.ledStatus.collectAsState()
        "Smart TV" -> viewModel.tvStatus.collectAsState()
        "Air Conditioner" -> viewModel.acStatus.collectAsState()
        "Speaker" -> viewModel.speakerStatus.collectAsState()
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
                Image(
                    painter = painterResource(R.drawable.lampicon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(8.dp)
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
                isCheckedInitial = deviceState,
                onTurnOn = {
                    when (deviceName) {
                        "Lamp" -> viewModel.turnOnLed()
                        "Smart TV" -> viewModel.turnOnTV()
                        "Air Conditioner" -> viewModel.turnOnAC()
                        "Speaker" -> viewModel.turnOnSpeaker()
                    }
                },
                onTurnOff = {
                    when (deviceName) {
                        "Lamp" -> viewModel.turnOffLed()
                        "Smart TV" -> viewModel.turnOffTV()
                        "Air Conditioner" -> viewModel.turnOffAC()
                        "Speaker" -> viewModel.turnOffSpeaker()
                    }
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
