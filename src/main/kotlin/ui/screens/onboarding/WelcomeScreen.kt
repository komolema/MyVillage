package ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import localization.SupportedLanguage
import org.koin.compose.koinInject
import ui.navigation.NavigationRoute
import viewmodel.OnboardingViewModel

@Composable
fun WelcomeScreen(navController: NavController) {
    val viewModel: OnboardingViewModel = koinInject()
    val strings = viewModel.strings
    val currentLanguage by viewModel.currentLanguage
    val supportedLanguages = viewModel.supportedLanguages

    var selectedLanguage by remember { mutableStateOf(currentLanguage) }
    var showLanguageDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = strings.onboardingWelcomeTitle,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = strings.onboardingWelcomeDescription,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Language selection
        Text(
            text = strings.onboardingSelectLanguage,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box {
            Button(
                onClick = { showLanguageDropdown = true },
                modifier = Modifier
                    .width(200.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(selectedLanguage.displayName)
            }

            DropdownMenu(
                expanded = showLanguageDropdown,
                onDismissRequest = { showLanguageDropdown = false },
                modifier = Modifier.width(200.dp)
            ) {
                supportedLanguages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language.displayName) },
                        onClick = {
                            selectedLanguage = language
                            viewModel.updateLanguage(language)
                            showLanguageDropdown = false
                        }
                    )
                }
            }
        }

        // Watch video button
        Button(
            onClick = { /* TODO: Implement video playback */ },
            modifier = Modifier
                .width(250.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(strings.onboardingWatchVideo)
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { 
                    viewModel.completeOnboarding()
                    navController.navigate(NavigationRoute.Dashboard.route) {
                        popUpTo(NavigationRoute.OnboardingWelcome.route) { inclusive = true }
                    }
                }
            ) {
                Text(strings.skip)
            }

            Button(
                onClick = { navController.navigate(NavigationRoute.OnboardingUserRole.route) }
            ) {
                Text(strings.getStarted)
            }
        }
    }
}
