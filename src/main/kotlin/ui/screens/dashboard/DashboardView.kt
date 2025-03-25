package ui.screens.dashboard

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.navigation.NavigationManager
import ui.navigation.NavigationRoute
import viewmodel.OnboardingViewModel

/**
 * JavaFX view for the dashboard screen.
 */
class DashboardView : BorderPane(), KoinComponent {
    private val navigationManager = NavigationManager.getInstance()
    private val onboardingViewModel: OnboardingViewModel by inject()
    
    init {
        // Set up the content
        setupLayout()
    }
    
    private fun setupLayout() {
        // Top navigation bar
        val topBar = createTopBar()
        top = topBar
        
        // Main content
        val mainContent = createMainContent()
        center = mainContent
    }
    
    private fun createTopBar(): HBox {
        val hbox = HBox(20.0)
        hbox.padding = Insets(20.0)
        hbox.style = "-fx-background-color: #f0f0f0;"
        
        // Title
        val titleLabel = Label("My Village Dashboard")
        titleLabel.font = Font.font(20.0)
        
        // Navigation buttons
        val residentButton = Button("Residents")
        residentButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.Resident)
        }
        
        val animalButton = Button("Animals")
        animalButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.Animal)
        }
        
        val resourceButton = Button("Resources")
        resourceButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.Resource)
        }
        
        // Only show admin button for administrators
        val adminButton = Button("Admin")
        adminButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.Admin)
        }
        adminButton.isVisible = onboardingViewModel.userRole == settings.UserRole.ADMINISTRATOR
        
        val settingsButton = Button("Settings")
        settingsButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.Settings)
        }
        
        // Add all components to the top bar
        hbox.children.addAll(
            titleLabel,
            residentButton,
            animalButton,
            resourceButton,
            adminButton,
            settingsButton
        )
        
        return hbox
    }
    
    private fun createMainContent(): VBox {
        val vbox = VBox(20.0)
        vbox.alignment = Pos.CENTER
        vbox.padding = Insets(50.0)
        
        // Welcome message
        val welcomeLabel = Label("Welcome to My Village")
        welcomeLabel.font = Font.font(32.0)
        welcomeLabel.textAlignment = TextAlignment.CENTER
        
        // Dashboard description
        val descriptionLabel = Label(
            "This is your dashboard where you can access all the features of the application. " +
            "Use the navigation buttons above to manage residents, animals, and resources."
        )
        descriptionLabel.isWrapText = true
        descriptionLabel.textAlignment = TextAlignment.CENTER
        descriptionLabel.font = Font.font(16.0)
        
        // Quick stats
        val statsLabel = Label("Quick Statistics")
        statsLabel.font = Font.font(24.0)
        statsLabel.textAlignment = TextAlignment.CENTER
        
        // Placeholder stats
        val residentsLabel = Label("Residents: --")
        val animalsLabel = Label("Animals: --")
        val resourcesLabel = Label("Resources: --")
        
        // Add all components to the main content
        vbox.children.addAll(
            welcomeLabel,
            descriptionLabel,
            statsLabel,
            residentsLabel,
            animalsLabel,
            resourcesLabel
        )
        
        return vbox
    }
}