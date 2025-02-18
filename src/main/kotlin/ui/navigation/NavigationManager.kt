package ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ui.screens.DashboardScreen
import ui.screens.admin.AdminScreen
import ui.screens.animal.AnimalScreen
import ui.screens.resident.ResidentScreen
import ui.screens.resident.WindowMode
import ui.screens.resource.ResourceScreen
import ui.screens.settings.SettingsScreen
import viewmodel.ResidentViewModel
import java.util.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val residentViewModel: ResidentViewModel = koinInject()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
        composable("resident") { ResidentScreen(navController, residentViewModel) }
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
