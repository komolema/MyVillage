package ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import ui.screens.DashboardScreen
import ui.screens.admin.AdminScreen
import ui.screens.animal.AnimalScreen
import ui.screens.login.LoginScreen
import ui.screens.onboarding.WelcomeScreen
import ui.screens.onboarding.UserRoleScreen
import ui.screens.onboarding.AdminSetupScreen
import ui.screens.resident.GlossaryScreen
import ui.screens.resident.ResidentScreen
import ui.screens.resident.ResidentWindow
import ui.screens.resident.WindowMode
import ui.screens.resource.ResourceScreen
import ui.screens.settings.SettingsScreen
import viewmodel.OnboardingViewModel
import viewmodel.ResidentViewModel
import viewmodel.ResidentWindowViewModel
import java.util.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navigationService = remember { NavigationService.create(navController) }
    val residentViewModel: ResidentViewModel = koinInject()
    val residentWindowViewModel: ResidentWindowViewModel = koinInject()
    val onboardingViewModel: OnboardingViewModel = koinInject()

    // Always start with the login screen
    val startDestination = NavigationRoute.Login.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavigationRoute.Login.route) {
            LoginScreen(navigationService.navController)
        }
        composable(NavigationRoute.Dashboard.route) { 
            DashboardScreen(navigationService.navController) 
        }
        composable(NavigationRoute.Resident.route) { 
            ResidentScreen(navigationService.navController, residentViewModel) 
        }
        composable("resident/{residentId}?mode={mode}") { backStackEntry ->
            val residentId = backStackEntry.arguments?.getString("residentId")?.let {
                if (it.isNotEmpty()) {
                    UUID.fromString(it)
                } else {
                    UUID.randomUUID()
                }
            }
            val mode = WindowMode.valueOf(backStackEntry.arguments?.getString("mode")?.uppercase() ?: "VIEW")
            ResidentWindow(
                residentId, 
                mode, 
                { navigationService.navigateBack() }, 
                residentWindowViewModel
            )
        }
        composable(NavigationRoute.Animal.route) { 
            AnimalScreen(navigationService.navController) 
        }
        composable(NavigationRoute.Resource.route) { 
            ResourceScreen(navigationService.navController) 
        }
        composable(NavigationRoute.Admin.route) { 
            AdminScreen(navigationService.navController) 
        }
        composable(NavigationRoute.Settings.route) { 
            SettingsScreen(navigationService.navController) 
        }
        composable("glossary/{residentId}") { backStackEntry ->
            val residentId = backStackEntry.arguments?.getString("residentId") ?: ""
            GlossaryScreen(
                navigationService.navController,
                residentWindowViewModel,
                residentId
            )
        }

        // Onboarding screens
        composable(NavigationRoute.OnboardingWelcome.route) {
            WelcomeScreen(navigationService.navController)
        }

        composable(NavigationRoute.OnboardingUserRole.route) {
            UserRoleScreen(navigationService.navController)
        }

        composable(NavigationRoute.OnboardingAdminConfig.route) {
            AdminSetupScreen(navigationService.navController)
        }

        composable(NavigationRoute.OnboardingFeatureTour.route) {
            // TODO: Implement FeatureTourScreen
            // For now, navigate to the next screen
            navController.navigate(NavigationRoute.OnboardingFirstAction.route)
        }

        composable(NavigationRoute.OnboardingFirstAction.route) {
            // TODO: Implement FirstActionScreen
            // For now, navigate to the next screen
            navController.navigate(NavigationRoute.OnboardingDashboardIntro.route)
        }

        composable(NavigationRoute.OnboardingDashboardIntro.route) {
            // TODO: Implement DashboardIntroScreen
            // For now, complete onboarding and navigate to Dashboard
            onboardingViewModel.completeOnboarding()
            navController.navigate(NavigationRoute.Dashboard.route) {
                popUpTo(NavigationRoute.OnboardingWelcome.route) { inclusive = true }
            }
        }
    }
}
