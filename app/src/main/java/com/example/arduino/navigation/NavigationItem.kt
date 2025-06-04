package com.example.arduino.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val label: String, val icon: ImageVector) {
    object Pocetak : NavigationItem("pocetak", "Početak", Icons.Filled.Home)
    object Upotreba : NavigationItem("upotreba", "Upotreba", Icons.Filled.BarChart)
    object Postavke : NavigationItem("postavke", "Postavke", Icons.Filled.Settings)
}
