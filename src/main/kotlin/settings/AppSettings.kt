package settings

import androidx.compose.runtime.mutableStateOf
import localization.SupportedLanguage
import localization.LocaleManager
import java.util.prefs.Preferences

data class AppSettings(
    var language: SupportedLanguage = SupportedLanguage.ENGLISH,
    var isDarkMode: Boolean = false,
    var onboardingCompleted: Boolean = false,
    var showOnboardingOnStartup: Boolean = true,
    var userRole: UserRole = UserRole.STANDARD
)

enum class UserRole {
    ADMINISTRATOR,
    STANDARD
}

object AppSettingsManager {
    private val preferences = Preferences.userRoot().node("myvillage")
    private val currentSettings = mutableStateOf(loadSettings())

    private fun loadSettings(): AppSettings {
        return AppSettings(
            language = SupportedLanguage.valueOf(
                preferences.get("language", SupportedLanguage.ENGLISH.name)
            ),
            isDarkMode = preferences.getBoolean("darkMode", false),
            onboardingCompleted = preferences.getBoolean("onboardingCompleted", false),
            showOnboardingOnStartup = preferences.getBoolean("showOnboardingOnStartup", true),
            userRole = UserRole.valueOf(
                preferences.get("userRole", UserRole.STANDARD.name)
            )
        )
    }

    fun saveSettings(settings: AppSettings) {
        preferences.put("language", settings.language.name)
        preferences.putBoolean("darkMode", settings.isDarkMode)
        preferences.putBoolean("onboardingCompleted", settings.onboardingCompleted)
        preferences.putBoolean("showOnboardingOnStartup", settings.showOnboardingOnStartup)
        preferences.put("userRole", settings.userRole.name)
        preferences.flush()
        currentSettings.value = settings
    }

    fun getCurrentSettings(): AppSettings = currentSettings.value

    fun updateLanguage(language: SupportedLanguage) {
        val settings = getCurrentSettings().copy(language = language)
        saveSettings(settings)
        LocaleManager.setLocale(language)
    }

    fun updateDarkMode(isDarkMode: Boolean) {
        val settings = getCurrentSettings().copy(isDarkMode = isDarkMode)
        saveSettings(settings)
    }

    fun updateOnboardingCompleted(completed: Boolean) {
        val settings = getCurrentSettings().copy(onboardingCompleted = completed)
        saveSettings(settings)
    }

    fun updateUserRole(role: UserRole) {
        val settings = getCurrentSettings().copy(userRole = role)
        saveSettings(settings)
    }

    fun updateShowOnboardingOnStartup(show: Boolean) {
        val settings = getCurrentSettings().copy(showOnboardingOnStartup = show)
        saveSettings(settings)
    }
}
