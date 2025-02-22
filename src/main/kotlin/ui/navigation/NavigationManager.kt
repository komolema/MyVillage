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
import ui.screens.resident.ResidentWindow
import ui.screens.resident.WindowMode
import ui.screens.resource.ResourceScreen
import ui.screens.settings.SettingsScreen
import viewmodel.ResidentViewModel
import viewmodel.ResidentWindowViewModel
import java.util.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val residentViewModel: ResidentViewModel = koinInject()
    val residentWindowViewModel: ResidentWindowViewModel = koinInject()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
        composable("resident") { ResidentScreen(navController, residentViewModel) }
        composable("resident/{residentId}?mode={mode}") { backStackEntry ->
            val residentId = backStackEntry.arguments?.getString("residentId")?.let {
                if (it.isNotEmpty()) {
                    UUID.fromString(it)
                } else {
                    UUID.randomUUID()
                }
            }
            val mode = WindowMode.valueOf(backStackEntry.arguments?.getString("mode")?.uppercase() ?: "VIEW")
            ResidentWindow(residentId, mode, { navController.popBackStack() }, residentWindowViewModel)
        }
        composable("animal") { AnimalScreen(navController) }
        composable("resource") { ResourceScreen(navController) }
        composable("admin") { AdminScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}