package ui.screens.resource

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import theme.RedButtonColor
import ui.components.navigation.ScreenWithAppBar

@Composable
fun ResourceScreen(navController: NavController) {
    ScreenWithAppBar("Resource Screen", { navController.navigate("dashboard") }, RedButtonColor) {
        Text("Resource Content")
    }
}