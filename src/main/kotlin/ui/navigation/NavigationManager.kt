package ui.navigation

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import viewmodel.OnboardingViewModel
import viewmodel.ResidentViewModel
import viewmodel.ResidentWindowViewModel
import java.util.*
import ui.screens.resident.WindowMode

/**
 * JavaFX Navigation Manager that handles navigation between screens.
 * This replaces the Compose Navigation system with a JavaFX-based one.
 */
class NavigationManager : KoinComponent {
    private val onboardingViewModel: OnboardingViewModel by inject()
    private val residentViewModel: ResidentViewModel by inject()
    private val residentWindowViewModel: ResidentWindowViewModel by inject()

    private lateinit var stage: Stage
    private lateinit var rootPane: BorderPane
    private val contentArea = StackPane()

    // Navigation history for back navigation
    private val navigationHistory = mutableListOf<NavigationRoute>()
    private var currentRoute: NavigationRoute? = null

    /**
     * Initialize the navigation manager with the primary stage
     */
    fun initialize(primaryStage: Stage) {
        stage = primaryStage
        rootPane = BorderPane()
        rootPane.center = contentArea

        // Set up the scene
        val scene = Scene(rootPane, 1200.0, 800.0)
        stage.scene = scene

        // Navigate to the initial screen
        val startDestination = if (onboardingViewModel.isOnboardingRequired()) {
            NavigationRoute.OnboardingWelcome
        } else {
            NavigationRoute.Login
        }

        navigateTo(startDestination)
    }

    /**
     * Navigate to a specific route
     */
    fun navigateTo(route: NavigationRoute) {
        // Add current route to history if it exists
        currentRoute?.let { navigationHistory.add(it) }
        currentRoute = route

        val view = when (route) {
            is NavigationRoute.Login -> createLoginView()
            is NavigationRoute.Dashboard -> createDashboardView()
            is NavigationRoute.Resident -> createResidentView()
            is NavigationRoute.ResidentDetail -> createResidentDetailView(route.residentId, route.mode)
            is NavigationRoute.Animal -> createAnimalView()
            is NavigationRoute.Resource -> createResourceView()
            is NavigationRoute.Admin -> createAdminView()
            is NavigationRoute.Settings -> createSettingsView()
            is NavigationRoute.OnboardingWelcome -> createWelcomeView()
            is NavigationRoute.OnboardingUserRole -> createUserRoleView()
            is NavigationRoute.OnboardingAdminConfig -> createAdminSetupView()
            is NavigationRoute.OnboardingFeatureTour -> {
                // TODO: Implement feature tour view
                // For now, navigate to the next screen
                navigateTo(NavigationRoute.OnboardingFirstAction)
                return
            }
            is NavigationRoute.OnboardingFirstAction -> {
                // TODO: Implement first action view
                // For now, navigate to the next screen
                navigateTo(NavigationRoute.OnboardingDashboardIntro)
                return
            }
            is NavigationRoute.OnboardingDashboardIntro -> {
                // TODO: Implement dashboard intro view
                // For now, complete onboarding and navigate to Dashboard
                onboardingViewModel.completeOnboarding()
                navigateTo(NavigationRoute.Dashboard)
                return
            }
            else -> {
                // Default to a placeholder view if route not implemented
                createPlaceholderView("Screen not implemented: ${route.route}")
            }
        }

        setContent(view)
    }

    /**
     * Navigate back to the previous screen
     */
    fun navigateBack() {
        if (navigationHistory.isNotEmpty()) {
            val previousRoute = navigationHistory.removeAt(navigationHistory.size - 1)
            currentRoute = previousRoute
            navigateTo(previousRoute)
        } else {
            // If no history, navigate to dashboard
            navigateTo(NavigationRoute.Dashboard)
        }
    }

    /**
     * Set the content of the main area
     */
    private fun setContent(view: Parent) {
        contentArea.children.clear()
        contentArea.children.add(view)
    }

    // Methods to create views
    private fun createLoginView(): Parent = ui.screens.login.LoginView()
    private fun createDashboardView(): Parent = ui.screens.dashboard.DashboardView()
    private fun createResidentView(): Parent = ui.screens.resident.ResidentView()
    private fun createResidentDetailView(residentId: UUID?, mode: WindowMode): Parent = 
        ui.screens.resident.ResidentDetailView(residentId, mode) { navigateBack() }
    private fun createAnimalView(): Parent = ui.screens.animal.AnimalView()
    private fun createResourceView(): Parent = ui.screens.resource.ResourceView()
    private fun createAdminView(): Parent = ui.screens.admin.AdminView()
    private fun createSettingsView(): Parent = ui.screens.settings.SettingsView()
    private fun createWelcomeView(): Parent = ui.screens.onboarding.WelcomeView()
    private fun createUserRoleView(): Parent = ui.screens.onboarding.UserRoleView()
    private fun createAdminSetupView(): Parent = ui.screens.onboarding.AdminSetupView()

    /**
     * Create a placeholder view with a label
     */
    private fun createPlaceholderView(text: String): Parent {
        val label = Label(text)
        label.style = "-fx-font-size: 20px; -fx-padding: 20px;"
        return label
    }

    companion object {
        private var instance: NavigationManager? = null

        fun getInstance(): NavigationManager {
            if (instance == null) {
                instance = NavigationManager()
            }
            return instance!!
        }
    }
}
