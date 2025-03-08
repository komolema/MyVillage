package localization

import androidx.compose.runtime.mutableStateOf
import java.util.*

enum class SupportedLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    SETSWANA("tn", "Setswana"),
    XHOSA("xh", "Xhosa"),
    SEPEDI("nso", "Sepedi")
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