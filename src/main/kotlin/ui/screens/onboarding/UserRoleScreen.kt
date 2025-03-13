package ui.screens.onboarding

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.compose.koinInject
import settings.UserRole
import ui.navigation.NavigationRoute
import viewmodel.OnboardingViewModel

@Composable
fun UserRoleScreen(navController: NavController) {
    val viewModel: OnboardingViewModel = koinInject()
    val strings = viewModel.strings
    val currentRole by viewModel.userRole

    var selectedRole by remember { mutableStateOf(currentRole) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = strings.onboardingUserRoleTitle,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = strings.onboardingUserRoleDescription,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Role selection cards
        RoleSelectionCard(
            title = strings.onboardingAdministratorRole,
            description = strings.onboardingAdministratorRoleDescription,
            isSelected = selectedRole == UserRole.ADMINISTRATOR,
            onClick = { 
                selectedRole = UserRole.ADMINISTRATOR
                viewModel.updateUserRole(UserRole.ADMINISTRATOR)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleSelectionCard(
            title = strings.onboardingStandardRole,
            description = strings.onboardingStandardRoleDescription,
            isSelected = selectedRole == UserRole.STANDARD,
            onClick = { 
                selectedRole = UserRole.STANDARD
                viewModel.updateUserRole(UserRole.STANDARD)
            }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { navController.navigateUp() }
            ) {
                Text(strings.back)
            }

            Button(
                onClick = {
                    if (selectedRole == UserRole.ADMINISTRATOR) {
                        navController.navigate(NavigationRoute.OnboardingAdminConfig.route)
                    } else {
                        navController.navigate(NavigationRoute.OnboardingFeatureTour.route)
                    }
                }
            ) {
                Text(strings.next)
            }
        }
    }
}

@Composable
fun RoleSelectionCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = description,
                fontSize = 16.sp
            )
        }
    }
}
