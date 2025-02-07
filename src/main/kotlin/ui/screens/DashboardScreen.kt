package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import theme.*

@Composable
fun DashboardScreen(navController: NavController) {
    val buttonModifier = Modifier
        .width(200.dp)
        .height(60.dp)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.navigate("resident") },
            colors = ButtonDefaults.buttonColors(BlueButtonColor),
            modifier = buttonModifier
        ) {
            Text("Resident")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("animal") },
            colors = ButtonDefaults.buttonColors(GreenButtonColor),
            modifier = buttonModifier
        ) {
            Text("Animal")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("resource") },
            colors = ButtonDefaults.buttonColors(RedButtonColor),
            modifier = buttonModifier
        ) {
            Text("Resource")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("admin") },
            colors = ButtonDefaults.buttonColors(YellowButtonColor),
            modifier = buttonModifier
        ) {
            Text("Admin")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("settings") },
            colors = ButtonDefaults.buttonColors(GrayButtonColor),
            modifier = buttonModifier
        ) {
            Text("Settings")
        }
    }
}