package ui.navigation

import java.util.UUID
import ui.screens.resident.WindowMode

sealed class NavigationRoute(val route: String) {
    data object Dashboard : NavigationRoute("dashboard")
    data object Resident : NavigationRoute("resident")
    data class ResidentDetail(val residentId: UUID? = null, val mode: WindowMode = WindowMode.VIEW) : 
        NavigationRoute("resident/${residentId ?: ""}?mode=${mode.name.lowercase()}")
    data object Animal : NavigationRoute("animal")
    data object Resource : NavigationRoute("resource")
    data object Admin : NavigationRoute("admin")
    data object Settings : NavigationRoute("settings")

    // Onboarding routes
    data object OnboardingWelcome : NavigationRoute("onboarding/welcome")
    data object OnboardingUserRole : NavigationRoute("onboarding/user-role")
    data object OnboardingAdminConfig : NavigationRoute("onboarding/admin-config")
    data object OnboardingFeatureTour : NavigationRoute("onboarding/feature-tour")
    data object OnboardingFirstAction : NavigationRoute("onboarding/first-action")
    data object OnboardingDashboardIntro : NavigationRoute("onboarding/dashboard-intro")
    data object OnboardingSecurity : NavigationRoute("onboarding/security")
    data object OnboardingHelp : NavigationRoute("onboarding/help")
    data object OnboardingComplete : NavigationRoute("onboarding/complete")

    companion object {
        fun fromRoute(route: String): NavigationRoute? {
            return when {
                route == Dashboard.route -> Dashboard
                route == Resident.route -> Resident
                route == Animal.route -> Animal
                route == Resource.route -> Resource
                route == Admin.route -> Admin
                route == Settings.route -> Settings
                route == OnboardingWelcome.route -> OnboardingWelcome
                route == OnboardingUserRole.route -> OnboardingUserRole
                route == OnboardingAdminConfig.route -> OnboardingAdminConfig
                route == OnboardingFeatureTour.route -> OnboardingFeatureTour
                route == OnboardingFirstAction.route -> OnboardingFirstAction
                route == OnboardingDashboardIntro.route -> OnboardingDashboardIntro
                route == OnboardingSecurity.route -> OnboardingSecurity
                route == OnboardingHelp.route -> OnboardingHelp
                route == OnboardingComplete.route -> OnboardingComplete
                route.startsWith("resident/") -> {
                    try {
                        // Split path and query
                        val (path, query) = route.split("?", limit = 2).let { parts ->
                            parts[0] to (parts.getOrNull(1) ?: "")
                        }

                        // Get UUID from path
                        val id = path.split("/").getOrNull(1)?.takeIf { it.isNotEmpty() }?.let { UUID.fromString(it) }

                        // Parse mode from query parameters
                        val mode = query.split("&").mapNotNull { param ->
                            param.split("=", limit = 2).let { kv ->
                                if (kv.size == 2 && kv[0] == "mode") kv[1] else null
                            }
                        }.firstOrNull()?.let { modeStr ->
                            try {
                                WindowMode.valueOf(modeStr.uppercase())
                            } catch (e: IllegalArgumentException) {
                                return null  // Invalid mode provided
                            }
                        } ?: WindowMode.VIEW

                        ResidentDetail(id, mode)
                    } catch (e: IllegalArgumentException) {
                        null  // Invalid UUID
                    }
                }
                else -> null
            }
        }
    }
}
