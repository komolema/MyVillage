package ui.screens.onboarding

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.navigation.NavigationManager
import ui.navigation.NavigationRoute
import viewmodel.OnboardingViewModel

/**
 * JavaFX view for the onboarding welcome screen.
 */
class WelcomeView : BorderPane(), KoinComponent {
    private val onboardingViewModel: OnboardingViewModel by inject()
    private val navigationManager = NavigationManager.getInstance()
    
    init {
        // Set up the welcome content
        val welcomeContent = createWelcomeContent()
        
        // Center the content in the view
        center = welcomeContent
    }
    
    private fun createWelcomeContent(): VBox {
        val vbox = VBox(20.0)
        vbox.alignment = Pos.CENTER
        vbox.padding = Insets(50.0)
        vbox.maxWidth = 600.0
        
        // Title
        val titleLabel = Label("Welcome to My Village")
        titleLabel.font = Font.font(28.0)
        titleLabel.textAlignment = TextAlignment.CENTER
        
        // Description
        val descriptionLabel = Label(
            "My Village is a comprehensive management system designed to help you " +
            "efficiently manage your village resources, residents, and animals. " +
            "Let's get started with a quick setup process."
        )
        descriptionLabel.isWrapText = true
        descriptionLabel.textAlignment = TextAlignment.CENTER
        descriptionLabel.font = Font.font(16.0)
        
        // Next button
        val nextButton = Button("Get Started")
        nextButton.prefWidth = 200.0
        nextButton.defaultButtonProperty().set(true)
        
        // Next button action
        nextButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.OnboardingUserRole)
        }
        
        // Skip button (only shown if not first startup)
        val skipButton = Button("Skip Onboarding")
        skipButton.prefWidth = 200.0
        skipButton.isVisible = !onboardingViewModel.isFirstStartup()
        
        // Skip button action
        skipButton.setOnAction {
            onboardingViewModel.completeOnboarding()
            navigationManager.navigateTo(NavigationRoute.Dashboard)
        }
        
        // Add all components to the form
        vbox.children.addAll(
            titleLabel,
            descriptionLabel,
            nextButton
        )
        
        // Only add skip button if not first startup
        if (!onboardingViewModel.isFirstStartup()) {
            vbox.children.add(skipButton)
        }
        
        return vbox
    }
}