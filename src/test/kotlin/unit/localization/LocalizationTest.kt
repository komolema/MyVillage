package unit.localization

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import localization.*
import settings.AppSettingsManager
import java.util.*

class LocalizationTest {
    private val initialLanguage = AppSettingsManager.getCurrentSettings().language

    @BeforeEach
    fun setup() {
        // Reset to English before each test
        LocaleManager.setLocale(SupportedLanguage.ENGLISH)
        AppSettingsManager.updateLanguage(SupportedLanguage.ENGLISH)
    }

    @AfterEach
    fun cleanup() {
        // Restore original language
        LocaleManager.setLocale(initialLanguage)
        AppSettingsManager.updateLanguage(initialLanguage)
    }
    @Test
    fun `test language switching`() {
        // Start with English
        LocaleManager.setLocale(SupportedLanguage.ENGLISH)
        assertEquals(SupportedLanguage.ENGLISH, LocaleManager.getCurrentLocale())

        // Switch to Setswana
        LocaleManager.setLocale(SupportedLanguage.SETSWANA)
        assertEquals(SupportedLanguage.SETSWANA, LocaleManager.getCurrentLocale())
        assertEquals(Locale(SupportedLanguage.SETSWANA.code), Locale.getDefault())
    }

    @Test
    fun `test string resources for all languages`() {
        val languages = SupportedLanguage.values()

        languages.forEach { language ->
            val resources = StringResourcesManager.getStringResources(language)
            assertNotNull(resources)

            // Test a few key strings in each language
            when (language) {
                SupportedLanguage.ENGLISH -> {
                    assertEquals("Save", resources.save)
                    assertEquals("Settings", resources.settings)
                    assertEquals("Next", resources.next)
                }
                SupportedLanguage.SETSWANA -> {
                    assertEquals("Boloka", resources.save)
                    assertEquals("Dipeakanyo", resources.settings)
                    assertEquals("E e latelang", resources.next)
                }
                SupportedLanguage.XHOSA -> {
                    assertEquals("Gcina", resources.save)
                    assertEquals("Iisethingi", resources.settings)
                    assertEquals("Okulandelayo", resources.next)
                }
                SupportedLanguage.SEPEDI -> {
                    assertEquals("Boloka", resources.save)
                    assertEquals("Dipeakanyo", resources.settings)
                    assertEquals("Ye e latelago", resources.next)
                }
                else -> {
                    // For other languages, just verify that the resources exist
                    assertNotNull(resources.save)
                    assertNotNull(resources.settings)
                    assertNotNull(resources.next)
                }
            }
        }
    }

    @Test
    fun `test dependent strings for all languages`() {
        val languages = SupportedLanguage.values()

        languages.forEach { language ->
            val resources = StringResourcesManager.getStringResources(language)
            assertNotNull(resources)

            when (language) {
                SupportedLanguage.ENGLISH -> {
                    assertEquals("Dependents Information", resources.dependentsInformation)
                    assertEquals("Add Dependent", resources.addDependent)
                    assertEquals("Edit Dependent", resources.editDependent)
                    assertEquals("Male", resources.genderMale)
                    assertEquals("Female", resources.genderFemale)
                    assertEquals("Other", resources.genderOther)
                }
                SupportedLanguage.SETSWANA -> {
                    assertEquals("Tshedimosetso ya Bana", resources.dependentsInformation)
                    assertEquals("Oketsa Ngwana", resources.addDependent)
                    assertEquals("Fetola Ngwana", resources.editDependent)
                    assertEquals("Monna", resources.genderMale)
                    assertEquals("Mosadi", resources.genderFemale)
                    assertEquals("Tse dingwe", resources.genderOther)
                }
                SupportedLanguage.XHOSA -> {
                    assertEquals("Ulwazi lwabaxhomekeki", resources.dependentsInformation)
                    assertEquals("Yongeza umxhomekeki", resources.addDependent)
                    assertEquals("Hlela umxhomekeki", resources.editDependent)
                    assertEquals("Indoda", resources.genderMale)
                    assertEquals("Umfazi", resources.genderFemale)
                    assertEquals("Okunye", resources.genderOther)
                }
                SupportedLanguage.SEPEDI -> {
                    assertEquals("Tshedimošo ya Bana", resources.dependentsInformation)
                    assertEquals("Oketša Ngwana", resources.addDependent)
                    assertEquals("Fetola Ngwana", resources.editDependent)
                    assertEquals("Monna", resources.genderMale)
                    assertEquals("Mosadi", resources.genderFemale)
                    assertEquals("Tše dingwe", resources.genderOther)
                }
                else -> {
                    // For other languages, just verify that the resources exist
                    assertNotNull(resources.dependentsInformation)
                    assertNotNull(resources.addDependent)
                    assertNotNull(resources.editDependent)
                    assertNotNull(resources.genderMale)
                    assertNotNull(resources.genderFemale)
                    assertNotNull(resources.genderOther)
                }
            }
        }
    }

    @Test
    fun `test residence strings for all languages`() {
        val languages = SupportedLanguage.values()

        languages.forEach { language ->
            val resources = StringResourcesManager.getStringResources(language)
            assertNotNull(resources)

            when (language) {
                SupportedLanguage.ENGLISH -> {
                    assertEquals("Residence Information", resources.residenceInformation)
                    assertEquals("Street", resources.street)
                    assertEquals("House Number", resources.houseNumber)
                    assertEquals("Optional", resources.optional)
                }
                SupportedLanguage.SETSWANA -> {
                    assertEquals("Tshedimosetso ya Bonno", resources.residenceInformation)
                    assertEquals("Mmila", resources.street)
                    assertEquals("Nomoro ya Ntlo", resources.houseNumber)
                    assertEquals("Ga e pateletsege", resources.optional)
                }
                SupportedLanguage.XHOSA -> {
                    assertEquals("Ulwazi lwendawo yokuhlala", resources.residenceInformation)
                    assertEquals("Isitalato", resources.street)
                    assertEquals("Inombolo yendlu", resources.houseNumber)
                    assertEquals("Ayinyanzelekanga", resources.optional)
                }
                SupportedLanguage.SEPEDI -> {
                    assertEquals("Tshedimošo ya Bodulo", resources.residenceInformation)
                    assertEquals("Mmila", resources.street)
                    assertEquals("Nomoro ya Ntlo", resources.houseNumber)
                    assertEquals("Ga e gapeletšwe", resources.optional)
                }
                else -> {
                    // For other languages, just verify that the resources exist
                    assertNotNull(resources.residenceInformation)
                    assertNotNull(resources.street)
                    assertNotNull(resources.houseNumber)
                    assertNotNull(resources.optional)
                }
            }
        }
    }

    @Test
    fun `test optional field formatting`() {
        val languages = SupportedLanguage.values()

        languages.forEach { language ->
            val resources = StringResourcesManager.getStringResources(language)
            val geoCoordinates = "${resources.geoCoordinates} (${resources.optional})"
            val landmark = "${resources.landmark} (${resources.optional})"

            // Verify the format matches what's used in the UI
            when (language) {
                SupportedLanguage.ENGLISH -> {
                    assertEquals("Geo Coordinates (Optional)", geoCoordinates)
                    assertEquals("Landmark (Optional)", landmark)
                }
                SupportedLanguage.SETSWANA -> {
                    assertEquals("Dikaelo tsa Lefatshe (Ga e pateletsege)", geoCoordinates)
                    assertEquals("Letshwao la Lefelo (Ga e pateletsege)", landmark)
                }
                SupportedLanguage.XHOSA -> {
                    assertEquals("Iindawo zomhlaba (Ayinyanzelekanga)", geoCoordinates)
                    assertEquals("Uphawu (Ayinyanzelekanga)", landmark)
                }
                SupportedLanguage.SEPEDI -> {
                    assertEquals("Maemo a Lefase (Ga e gapeletšwe)", geoCoordinates)
                    assertEquals("Leswao la Lefelo (Ga e gapeletšwe)", landmark)
                }
                else -> {
                    // For other languages, just verify that the format is correct
                    assertTrue(geoCoordinates.contains(resources.geoCoordinates))
                    assertTrue(geoCoordinates.contains(resources.optional))
                    assertTrue(landmark.contains(resources.landmark))
                    assertTrue(landmark.contains(resources.optional))
                }
            }
        }
    }

    @Test
    fun `test immediate string updates`() {
        // Start with English
        LocaleManager.setLocale(SupportedLanguage.ENGLISH)
        var strings = StringResourcesManager.getCurrentStringResources()
        assertEquals("Residence Information", strings.residenceInformation)

        // Change to Setswana
        LocaleManager.setLocale(SupportedLanguage.SETSWANA)
        strings = StringResourcesManager.getCurrentStringResources()
        assertEquals("Tshedimosetso ya Bonno", strings.residenceInformation)

        // Change to Xhosa
        LocaleManager.setLocale(SupportedLanguage.XHOSA)
        strings = StringResourcesManager.getCurrentStringResources()
        assertEquals("Ulwazi lwendawo yokuhlala", strings.residenceInformation)

        // Change back to English
        LocaleManager.setLocale(SupportedLanguage.ENGLISH)
        strings = StringResourcesManager.getCurrentStringResources()
        assertEquals("Residence Information", strings.residenceInformation)
    }

    @Test
    fun `test language persistence`() {
        // Set language to Xhosa
        AppSettingsManager.updateLanguage(SupportedLanguage.XHOSA)

        // Verify it's saved
        val settings = AppSettingsManager.getCurrentSettings()
        assertEquals(SupportedLanguage.XHOSA, settings.language)

        // Verify strings are in Xhosa
        val strings = StringResourcesManager.getCurrentStringResources()
        assertEquals("Gcina", strings.save)
        assertEquals("Iisethingi", strings.settings)
    }
}
