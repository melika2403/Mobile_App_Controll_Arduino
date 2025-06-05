package com.example.arduino

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.arduino.navigation.NavigationGraph
import com.example.arduino.ui.MainScreen
import com.example.arduino.ui.MainTopBar
import com.example.arduino.ui.MainViewModel
import com.example.arduino.ui.SplashScreen
import com.example.arduino.ui.navigation.BottomNavigationBar
import com.example.arduino.ui.theme.ArduinoTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArduinoTheme {
                var showSplash by remember { mutableStateOf(true) }
                val navController = rememberNavController()

                if (showSplash) {
                    SplashScreen()
                    LaunchedEffect(Unit) {
                        delay(2500)  // trajanje splash screena
                        showSplash = false
                    }
                } else {
                    Scaffold(
                        topBar = {
                            MainTopBar(onAboutClick = {
                                navController.navigate("about")
                            })
                        },
                        bottomBar = { BottomNavigationBar(navController = navController) }
                    ) { innerPadding ->
                        NavigationGraph(
                            navController = navController,
                            viewModel = viewModel,
                            innerPadding = innerPadding
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnect()
    }
}
