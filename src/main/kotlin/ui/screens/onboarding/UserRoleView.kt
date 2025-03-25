package ui.screens.onboarding

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import settings.UserRole
import ui.navigation.NavigationManager
import ui.navigation.NavigationRoute
import viewmodel.OnboardingViewModel

/**
 * JavaFX view for the user role selection screen in onboarding.
 */
class UserRoleView : BorderPane(), KoinComponent {
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
        val titleLabel = Label("Select Your Role")
        titleLabel.font = Font.font(28.0)
        titleLabel.textAlignment = TextAlignment.CENTER

        // Description
        val descriptionLabel = Label(
            "Please select your role in the village. This will determine " +
            "the features and permissions available to you."
        )
        descriptionLabel.isWrapText = true
        descriptionLabel.textAlignment = TextAlignment.CENTER
        descriptionLabel.font = Font.font(16.0)

        // Role selection
        val roleToggleGroup = ToggleGroup()

        val administratorRadio = RadioButton("Administrator")
        administratorRadio.toggleGroup = roleToggleGroup
        administratorRadio.userData = UserRole.ADMINISTRATOR
        administratorRadio.isSelected = onboardingViewModel.userRole == UserRole.ADMINISTRATOR

        val standardRadio = RadioButton("Standard User")
        standardRadio.toggleGroup = roleToggleGroup
        standardRadio.userData = UserRole.STANDARD
        standardRadio.isSelected = onboardingViewModel.userRole == UserRole.STANDARD

        // Role descriptions
        val roleDescriptionLabel = Label(getRoleDescription(onboardingViewModel.userRole))
        roleDescriptionLabel.isWrapText = true
        roleDescriptionLabel.textAlignment = TextAlignment.CENTER
        roleDescriptionLabel.font = Font.font(14.0)

        // Update description when role changes
        roleToggleGroup.selectedToggleProperty().addListener { _, _, newValue ->
            val selectedRole = newValue?.userData as? UserRole ?: UserRole.STANDARD
            roleDescriptionLabel.text = getRoleDescription(selectedRole)
        }

        // Next button
        val nextButton = Button("Next")
        nextButton.prefWidth = 200.0
        nextButton.defaultButtonProperty().set(true)

        // Next button action
        nextButton.setOnAction {
            val selectedRole = roleToggleGroup.selectedToggle?.userData as? UserRole ?: UserRole.STANDARD
            onboardingViewModel.updateUserRole(selectedRole)

            // Navigate to admin setup if administrator, otherwise skip to feature tour
            if (selectedRole == UserRole.ADMINISTRATOR) {
                navigationManager.navigateTo(NavigationRoute.OnboardingAdminConfig)
            } else {
                navigationManager.navigateTo(NavigationRoute.OnboardingFeatureTour)
            }
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
            administratorRadio,
            standardRadio,
            roleDescriptionLabel,
            nextButton,
            backButton
        )

        return vbox
    }

    private fun getRoleDescription(role: UserRole): String {
        return when (role) {
            UserRole.ADMINISTRATOR -> 
                "Administrators have full access to all features and can manage users, settings, and system configuration."
            UserRole.STANDARD -> 
                "Standard users can view and update information about residents, animals, and resources, but cannot change system settings."
        }
    }
}
