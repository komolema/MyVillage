package viewmodel

import database.DatabaseManager
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import localization.LocaleManager
import localization.SupportedLanguage
import localization.StringResourcesManager
import settings.AppSettingsManager
import settings.UserRole

class OnboardingViewModel {
    // JavaFX properties
    val currentLanguageProperty = SimpleObjectProperty(AppSettingsManager.getCurrentSettings().language)
    val userRoleProperty = SimpleObjectProperty(AppSettingsManager.getCurrentSettings().userRole)
    val onboardingCompletedProperty = SimpleBooleanProperty(AppSettingsManager.getCurrentSettings().onboardingCompleted)
    val showOnboardingOnStartupProperty = SimpleBooleanProperty(AppSettingsManager.getCurrentSettings().showOnboardingOnStartup)
    val villageNameProperty = SimpleStringProperty("")
    val villageLocationProperty = SimpleStringProperty("")
    val adminContactProperty = SimpleStringProperty("")

    // Convenience getters
    val currentLanguage: SupportedLanguage get() = currentLanguageProperty.get()
    val userRole: UserRole get() = userRoleProperty.get()
    val onboardingCompleted: Boolean get() = onboardingCompletedProperty.get()
    val showOnboardingOnStartup: Boolean get() = showOnboardingOnStartupProperty.get()
    val villageName: String get() = villageNameProperty.get()
    val villageLocation: String get() = villageLocationProperty.get()
    val adminContact: String get() = adminContactProperty.get()

    val supportedLanguages = LocaleManager.getSupportedLanguages()
    private val stringsProperty = SimpleObjectProperty(StringResourcesManager.getCurrentStringResources())
    val strings get() = stringsProperty.get()

    fun updateLanguage(language: SupportedLanguage) {
        stringsProperty.set(StringResourcesManager.getStringResources(language))
        currentLanguageProperty.set(language)
        LocaleManager.setLocale(language)
        AppSettingsManager.updateLanguage(language)
    }

    fun updateUserRole(role: UserRole) {
        userRoleProperty.set(role)
        AppSettingsManager.updateUserRole(role)
    }

    fun updateVillageName(name: String) {
        villageNameProperty.set(name)
    }

    fun updateVillageLocation(location: String) {
        villageLocationProperty.set(location)
    }

    fun updateAdminContact(contact: String) {
        adminContactProperty.set(contact)
    }

    fun completeOnboarding() {
        onboardingCompletedProperty.set(true)
        AppSettingsManager.updateOnboardingCompleted(true)
    }

    fun isOnboardingRequired(): Boolean {
        val settings = AppSettingsManager.getCurrentSettings()
        // Always show onboarding on first startup
        if (DatabaseManager.isFirstStartup()) {
            return true
        }
        return settings.showOnboardingOnStartup && !settings.onboardingCompleted
    }

    /**
     * Check if this is the first startup of the application.
     * This is used to determine whether to show the admin setup screen.
     */
    fun isFirstStartup(): Boolean {
        return DatabaseManager.isFirstStartup()
    }

    fun updateShowOnboardingOnStartup(show: Boolean) {
        showOnboardingOnStartupProperty.set(show)
        AppSettingsManager.updateShowOnboardingOnStartup(show)
    }
}
