package com.example.arduino.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.arduino.data.AppDatabase
import com.example.arduino.ui.DeviceUsageScreen
import com.example.arduino.ui.DeviceUsageViewModel
import com.example.arduino.ui.DeviceUsageViewModelFactory
import com.example.arduino.ui.MainScreen
import com.example.arduino.ui.MainViewModel
import com.example.arduino.ui.SettingsScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(navController: NavHostController, viewModel: MainViewModel, innerPadding: PaddingValues) {
    NavHost(navController, startDestination = NavigationItem.Pocetak.route) {
        composable(NavigationItem.Pocetak.route) {
            MainScreen(viewModel = viewModel)
        }
        composable(NavigationItem.Upotreba.route) {
            val context = LocalContext.current
            val dao = remember {
                AppDatabase.getInstance(context).deviceUsageLogDao()
            }
            val viewModel: DeviceUsageViewModel = viewModel(
                factory = DeviceUsageViewModelFactory(dao)
            )

            DeviceUsageScreen(viewModel = viewModel)
        }
        composable(NavigationItem.Postavke.route) {
            val currentSettings by viewModel.settings.collectAsState()
            val channels = viewModel.channels

            SettingsScreen(
                channels = channels,
                currentSettings = currentSettings,
                onSettingsChange = { updated ->
                    viewModel.updateSettings(updated)
                },
                onSubmit = {
                    // Ako trebaš još nešto kad korisnik klikne "Save"
                }
            )
        }

    }
}
