package com.example.arduino

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.example.arduino.navigation.NavigationGraph
import com.example.arduino.ui.MainScreen
import com.example.arduino.ui.MainViewModel
import com.example.arduino.ui.navigation.BottomNavigationBar
import com.example.arduino.ui.theme.ArduinoTheme

class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArduinoTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { innerPadding ->
                    NavigationGraph(navController = navController, viewModel = viewModel, innerPadding = innerPadding,)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnect()
    }
}

