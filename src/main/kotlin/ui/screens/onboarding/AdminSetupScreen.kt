package ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.compose.koinInject
import org.koin.java.KoinJavaComponent.inject
import security.VillageSecurityManager
import ui.navigation.NavigationRoute
import viewmodel.OnboardingViewModel

@Composable
fun AdminSetupScreen(navController: NavController) {
    val viewModel: OnboardingViewModel = koinInject()
    val securityManager: VillageSecurityManager = koinInject()
    val strings = viewModel.strings
    
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var villageName by remember { mutableStateOf("") }
    
    var passwordError by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Setup Administrator Account",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = "As the village chief, you will be the administrator of the system. Please provide your details to create your account.",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Form fields
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = password != confirmPassword && confirmPassword.isNotEmpty()
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                passwordError = password != confirmPassword
            },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        if (passwordError) {
            Text(
                text = "Passwords do not match",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )
        }
        
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number (Optional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = villageName,
            onValueChange = { villageName = it },
            label = { Text("Village Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        
        if (formError != null) {
            Text(
                text = formError!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 16.dp)
            )
        }
        
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
                    // Validate form
                    when {
                        username.isBlank() -> formError = "Username is required"
                        password.isBlank() -> formError = "Password is required"
                        password != confirmPassword -> formError = "Passwords do not match"
                        firstName.isBlank() -> formError = "First name is required"
                        lastName.isBlank() -> formError = "Last name is required"
                        email.isBlank() -> formError = "Email is required"
                        villageName.isBlank() -> formError = "Village name is required"
                        else -> {
                            formError = null
                            isLoading = true
                            
                            try {
                                // Create admin user
                                val userId = securityManager.createAdminUser(
                                    username = username,
                                    password = password,
                                    firstName = firstName,
                                    lastName = lastName,
                                    email = email,
                                    phoneNumber = if (phoneNumber.isBlank()) null else phoneNumber
                                )
                                
                                // Create leadership record
                                securityManager.createChiefLeadership(
                                    firstName = firstName,
                                    lastName = lastName,
                                    villageName = villageName
                                )
                                
                                // Mark onboarding as completed
                                viewModel.completeOnboarding()
                                
                                // Navigate to dashboard
                                navController.navigate(NavigationRoute.Dashboard.route) {
                                    popUpTo(NavigationRoute.OnboardingWelcome.route) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                formError = "Error creating admin user: ${e.message}"
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Account")
                }
            }
        }
    }
}