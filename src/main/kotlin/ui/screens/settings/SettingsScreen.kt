package ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import viewmodel.SettingsViewModel
import ui.components.navigation.ScreenWithAppBar
import theme.GrayButtonColor
import localization.SupportedLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel = remember { SettingsViewModel() }
    val strings = viewModel.strings

    // Function to convert country code to flag emoji
    fun countryCodeToFlag(countryCode: String): String {
        val firstLetter = Character.codePointAt(countryCode, 0) - 0x61 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode, 1) - 0x61 + 0x1F1E6
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

    ScreenWithAppBar(strings.settings, { navController.navigate("dashboard") }, GrayButtonColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = strings.language,
                        style = MaterialTheme.typography.titleMedium
                    )

                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        TextField(
                            value = "${countryCodeToFlag(viewModel.currentLanguage.value.countryCode)} ${viewModel.currentLanguage.value.displayName}",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            viewModel.supportedLanguages.forEach { language ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(countryCodeToFlag(language.countryCode))
                                            Text(language.displayName)
                                        }
                                    },
                                    onClick = {
                                        viewModel.updateLanguage(language)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Appearance Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = strings.appearance,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(strings.darkMode)
                        Switch(
                            checked = viewModel.isDarkMode.value,
                            onCheckedChange = { viewModel.updateDarkMode(it) }
                        )
                    }
                }
            }

            // Onboarding Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Onboarding",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show onboarding on startup")
                        Switch(
                            checked = viewModel.showOnboardingOnStartup.value,
                            onCheckedChange = { viewModel.updateShowOnboardingOnStartup(it) }
                        )
                    }
                }
            }
        }
    }
}
