package localization

import androidx.compose.runtime.mutableStateOf
import java.util.*

enum class SupportedLanguage(val code: String, val displayName: String, val countryCode: String) {
    ENGLISH("en", "English", "gb"),
    SETSWANA("tn", "Setswana", "za"),
    XHOSA("xh", "Xhosa", "za"),
    SEPEDI("nso", "Sepedi", "za"),
    SHONA("sn", "Shona", "zw"),
    SWAHILI("sw", "Swahili", "tz"),
    AMHARIC("am", "Amharic", "et"),
    YORUBA("yo", "Yoruba", "ng")
}

object LocaleManager {
    private val currentLocale = mutableStateOf(SupportedLanguage.ENGLISH)

    fun getCurrentLocale(): SupportedLanguage = currentLocale.value

    fun setLocale(language: SupportedLanguage) {
        currentLocale.value = language
        // Update the system locale
        Locale.setDefault(Locale(language.code))
    }

    fun getSupportedLanguages(): List<SupportedLanguage> = SupportedLanguage.values().toList()
}
