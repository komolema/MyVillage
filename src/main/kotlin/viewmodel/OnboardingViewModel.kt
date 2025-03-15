package viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import database.DatabaseManager
import localization.LocaleManager
import localization.SupportedLanguage
import localization.StringResourcesManager
import settings.AppSettingsManager
import settings.UserRole

class OnboardingViewModel {
    private val _currentLanguage = mutableStateOf(AppSettingsManager.getCurrentSettings().language)
    val currentLanguage: State<SupportedLanguage> = _currentLanguage

    private val _userRole = mutableStateOf(AppSettingsManager.getCurrentSettings().userRole)
    val userRole: State<UserRole> = _userRole

    private val _onboardingCompleted = mutableStateOf(AppSettingsManager.getCurrentSettings().onboardingCompleted)
    val onboardingCompleted: State<Boolean> = _onboardingCompleted

    private val _showOnboardingOnStartup = mutableStateOf(AppSettingsManager.getCurrentSettings().showOnboardingOnStartup)
    val showOnboardingOnStartup: State<Boolean> = _showOnboardingOnStartup

    private val _villageName = mutableStateOf("")
    val villageName: State<String> = _villageName

    private val _villageLocation = mutableStateOf("")
    val villageLocation: State<String> = _villageLocation

    private val _adminContact = mutableStateOf("")
    val adminContact: State<String> = _adminContact

    val supportedLanguages = LocaleManager.getSupportedLanguages()
    private val _strings = mutableStateOf(StringResourcesManager.getCurrentStringResources())
    val strings get() = _strings.value

    fun updateLanguage(language: SupportedLanguage) {
        _strings.value = StringResourcesManager.getStringResources(language)
        _currentLanguage.value = language
        LocaleManager.setLocale(language)
        AppSettingsManager.updateLanguage(language)
    }

    fun updateUserRole(role: UserRole) {
        _userRole.value = role
        AppSettingsManager.updateUserRole(role)
    }

    fun updateVillageName(name: String) {
        _villageName.value = name
    }

    fun updateVillageLocation(location: String) {
        _villageLocation.value = location
    }

    fun updateAdminContact(contact: String) {
        _adminContact.value = contact
    }

    fun completeOnboarding() {
        _onboardingCompleted.value = true
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
        _showOnboardingOnStartup.value = show
        AppSettingsManager.updateShowOnboardingOnStartup(show)
    }
}
