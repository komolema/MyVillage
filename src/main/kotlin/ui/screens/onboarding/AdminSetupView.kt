package ui.screens.onboarding

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
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
 * JavaFX view for the admin setup screen in onboarding.
 */
class AdminSetupView : BorderPane(), KoinComponent {
    private val onboardingViewModel: OnboardingViewModel by inject()
    private val navigationManager = NavigationManager.getInstance()
    
    init {
        // Set up the content
        val content = createContent()
        
        // Center the content in the view
        center = content
    }
    
    private fun createContent(): VBox {
        val vbox = VBox(20.0)
        vbox.alignment = Pos.CENTER
        vbox.padding = Insets(50.0)
        vbox.maxWidth = 600.0
        
        // Title
        val titleLabel = Label("Administrator Setup")
        titleLabel.font = Font.font(28.0)
        titleLabel.textAlignment = TextAlignment.CENTER
        
        // Description
        val descriptionLabel = Label(
            "As an administrator, you can configure additional settings for your village. " +
            "Please provide the following information."
        )
        descriptionLabel.isWrapText = true
        descriptionLabel.textAlignment = TextAlignment.CENTER
        descriptionLabel.font = Font.font(16.0)
        
        // Village name field
        val villageNameLabel = Label("Village Name")
        val villageNameField = TextField()
        villageNameField.promptText = "Enter village name"
        villageNameField.textProperty().bindBidirectional(onboardingViewModel.villageNameProperty)
        
        // Village location field
        val villageLocationLabel = Label("Village Location")
        val villageLocationField = TextField()
        villageLocationField.promptText = "Enter village location"
        villageLocationField.textProperty().bindBidirectional(onboardingViewModel.villageLocationProperty)
        
        // Admin contact field
        val adminContactLabel = Label("Administrator Contact")
        val adminContactField = TextField()
        adminContactField.promptText = "Enter administrator contact information"
        adminContactField.textProperty().bindBidirectional(onboardingViewModel.adminContactProperty)
        
        // Next button
        val nextButton = Button("Next")
        nextButton.prefWidth = 200.0
        nextButton.defaultButtonProperty().set(true)
        
        // Next button action
        nextButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.OnboardingFeatureTour)
        }
        
        // Back button
        val backButton = Button("Back")
        backButton.prefWidth = 200.0
        
        // Back button action
        backButton.setOnAction {
            navigationManager.navigateBack()
        }
        
        // Add all components to the form
        vbox.children.addAll(
            titleLabel,
            descriptionLabel,
            villageNameLabel,
            villageNameField,
            villageLocationLabel,
            villageLocationField,
            adminContactLabel,
            adminContactField,
            nextButton,
            backButton
        )
        
        return vbox
    }
}