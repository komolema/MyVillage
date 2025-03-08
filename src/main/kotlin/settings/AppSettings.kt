package settings

import androidx.compose.runtime.mutableStateOf
import localization.SupportedLanguage
import localization.LocaleManager
import java.util.prefs.Preferences

data class AppSettings(
    var language: SupportedLanguage = SupportedLanguage.ENGLISH,
    var isDarkMode: Boolean = false
)

object AppSettingsManager {
    private val preferences = Preferences.userRoot().node("myvillage")
    private val currentSettings = mutableStateOf(loadSettings())

    private fun loadSettings(): AppSettings {
        return AppSettings(
            language = SupportedLanguage.valueOf(
                preferences.get("language", SupportedLanguage.ENGLISH.name)
            ),
            isDarkMode = preferences.getBoolean("darkMode", false)
        )
    }

    fun saveSettings(settings: AppSettings) {
        preferences.put("language", settings.language.name)
        preferences.putBoolean("darkMode", settings.isDarkMode)
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
}
