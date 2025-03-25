package ui.screens.settings

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ui.navigation.NavigationManager
import ui.navigation.NavigationRoute
import viewmodel.SettingsViewModel
import localization.SupportedLanguage
import javafx.collections.FXCollections
import javafx.util.StringConverter

/**
 * JavaFX view for the settings screen.
 */
class SettingsView : BorderPane(), KoinComponent {
    private val navigationManager = NavigationManager.getInstance()
    private val viewModel = SettingsViewModel()

    // UI components that need to be accessed from multiple methods
    private lateinit var titleLabel: Label
    private lateinit var languageTitle: Label
    private lateinit var appearanceTitle: Label
    private lateinit var darkModeLabel: Label

    init {
        // Set up the layout
        setupLayout()
    }

    private fun setupLayout() {
        // Top bar with title and back button
        val topBar = HBox(20.0)
        topBar.padding = Insets(20.0)
        topBar.style = "-fx-background-color: #f0f0f0;"

        titleLabel = Label(viewModel.strings.settings)
        titleLabel.font = Font.font("System", FontWeight.BOLD, 20.0)

        val backButton = Button("Back to Dashboard")
        backButton.setOnAction {
            navigationManager.navigateTo(NavigationRoute.Dashboard)
        }

        topBar.children.addAll(titleLabel, backButton)
        top = topBar

        // Main content area
        val contentBox = VBox(16.0)
        contentBox.padding = Insets(20.0)

        // Language Section
        val languageCard = createCard()
        val languageContent = VBox(8.0)
        languageContent.padding = Insets(16.0)

        languageTitle = Label(viewModel.strings.language)
        languageTitle.font = Font.font("System", FontWeight.BOLD, 16.0)

        // Language dropdown
        val languageComboBox = ComboBox<SupportedLanguage>()
        languageComboBox.items = FXCollections.observableArrayList(viewModel.supportedLanguages)
        languageComboBox.selectionModel.select(viewModel.currentLanguage.value)

        // Custom cell factory to display language with flag emoji
        languageComboBox.setConverter(object : StringConverter<SupportedLanguage>() {
            override fun toString(language: SupportedLanguage?): String {
                if (language == null) return ""
                return "${countryCodeToFlag(language.countryCode)} ${language.displayName}"
            }

            override fun fromString(string: String?): SupportedLanguage? {
                return null // Not needed for this use case
            }
        })

        languageComboBox.setOnAction {
            val selectedLanguage = languageComboBox.selectionModel.selectedItem
            if (selectedLanguage != null) {
                viewModel.updateLanguage(selectedLanguage)
                // Update UI with new language
                titleLabel.text = viewModel.strings.settings
                languageTitle.text = viewModel.strings.language
                appearanceTitle.text = viewModel.strings.appearance
                darkModeLabel.text = viewModel.strings.darkMode
            }
        }

        languageContent.children.addAll(languageTitle, languageComboBox)
        languageCard.children.add(languageContent)

        // Appearance Section
        val appearanceCard = createCard()
        val appearanceContent = VBox(8.0)
        appearanceContent.padding = Insets(16.0)

        appearanceTitle = Label(viewModel.strings.appearance)
        appearanceTitle.font = Font.font("System", FontWeight.BOLD, 16.0)

        // Dark mode toggle
        val darkModeBox = HBox(10.0)
        darkModeBox.alignment = Pos.CENTER_LEFT

        darkModeLabel = Label(viewModel.strings.darkMode)
        val darkModeToggle = CheckBox()
        darkModeToggle.isSelected = viewModel.isDarkMode.value
        HBox.setHgrow(darkModeLabel, Priority.ALWAYS)

        darkModeToggle.setOnAction {
            viewModel.updateDarkMode(darkModeToggle.isSelected)
        }

        darkModeBox.children.addAll(darkModeLabel, darkModeToggle)
        appearanceContent.children.addAll(appearanceTitle, darkModeBox)
        appearanceCard.children.add(appearanceContent)

        // Onboarding Section
        val onboardingCard = createCard()
        val onboardingContent = VBox(8.0)
        onboardingContent.padding = Insets(16.0)

        val onboardingTitle = Label("Onboarding")
        onboardingTitle.font = Font.font("System", FontWeight.BOLD, 16.0)

        // Onboarding toggle
        val onboardingBox = HBox(10.0)
        onboardingBox.alignment = Pos.CENTER_LEFT

        val onboardingLabel = Label("Show onboarding on startup")
        val onboardingToggle = CheckBox()
        onboardingToggle.isSelected = viewModel.showOnboardingOnStartup.value
        HBox.setHgrow(onboardingLabel, Priority.ALWAYS)

        onboardingToggle.setOnAction {
            viewModel.updateShowOnboardingOnStartup(onboardingToggle.isSelected)
        }

        onboardingBox.children.addAll(onboardingLabel, onboardingToggle)
        onboardingContent.children.addAll(onboardingTitle, onboardingBox)
        onboardingCard.children.add(onboardingContent)

        // Add all sections to content box
        contentBox.children.addAll(languageCard, appearanceCard, onboardingCard)
        center = contentBox
    }

    private fun createCard(): VBox {
        val card = VBox()
        card.style = "-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;"
        card.padding = Insets(1.0)
        card.maxWidth = Double.MAX_VALUE
        return card
    }

    // Function to convert country code to flag emoji
    private fun countryCodeToFlag(countryCode: String): String {
        val firstLetter = Character.codePointAt(countryCode, 0) - 0x61 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode, 1) - 0x61 + 0x1F1E6
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }
}
