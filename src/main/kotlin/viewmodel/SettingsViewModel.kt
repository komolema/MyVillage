package viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import localization.LocaleManager
import localization.SupportedLanguage
import localization.StringResourcesManager
import settings.AppSettingsManager

class SettingsViewModel {
    private val _currentLanguage = mutableStateOf(AppSettingsManager.getCurrentSettings().language)
    val currentLanguage: State<SupportedLanguage> = _currentLanguage

    private val _isDarkMode = mutableStateOf(AppSettingsManager.getCurrentSettings().isDarkMode)
    val isDarkMode: State<Boolean> = _isDarkMode

    private val _showOnboardingOnStartup = mutableStateOf(AppSettingsManager.getCurrentSettings().showOnboardingOnStartup)
    val showOnboardingOnStartup: State<Boolean> = _showOnboardingOnStartup

    val supportedLanguages = LocaleManager.getSupportedLanguages()
    private val _strings = mutableStateOf(StringResourcesManager.getCurrentStringResources())
    val strings get() = _strings.value

    fun updateLanguage(language: SupportedLanguage) {
        _strings.value = StringResourcesManager.getStringResources(language)
        _currentLanguage.value = language
        LocaleManager.setLocale(language)
        AppSettingsManager.updateLanguage(language)
    }

    fun updateDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        AppSettingsManager.updateDarkMode(enabled)
    }

    fun updateShowOnboardingOnStartup(show: Boolean) {
        _showOnboardingOnStartup.value = show
        AppSettingsManager.updateShowOnboardingOnStartup(show)
    }
}
