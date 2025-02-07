package ui.screens.resident

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import theme.BlueButtonColor
import ui.components.navigation.ScreenWithAppBar

@Composable
fun ResidentScreen(navController: NavController) {
    ScreenWithAppBar("Resident Screen", { navController.navigate("dashboard") }, BlueButtonColor) {
        Text("Resident Content")
    }
}