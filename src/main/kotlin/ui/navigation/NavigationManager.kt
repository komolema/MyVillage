package ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ui.screens.DashboardScreen
import ui.screens.admin.AdminScreen
import ui.screens.animal.AnimalScreen
import ui.screens.resident.ResidentScreen
import ui.screens.resource.ResourceScreen
import ui.screens.settings.SettingsScreen
import java.util.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
        composable("resident") { ResidentScreen(navController) }
        composable("animal") { AnimalScreen(navController) }
        composable("resource") { ResourceScreen(navController) }
        composable("admin") { AdminScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}

fun NavHostController.navigateToResidentWindow(residentId: UUID?, mode: WindowMode) {
    navigate("resident/${residentId ?: "new"}?mode=${mode.name.lowercase()}") {
        launchSingleTop = true
    }
}
