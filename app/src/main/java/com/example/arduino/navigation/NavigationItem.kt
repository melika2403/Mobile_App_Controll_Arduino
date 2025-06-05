package com.example.arduino.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val label: String, val icon: ImageVector) {
    object Pocetak : NavigationItem("pocetak", "Home", Icons.Filled.Home)
    object Upotreba : NavigationItem("upotreba", "Usage", Icons.Filled.BarChart)
    object Postavke : NavigationItem("postavke", "Settings", Icons.Filled.Settings)
    object ONama : NavigationItem("about", "About", Icons.Filled.Info)
}
