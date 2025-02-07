package ui.screens.admin

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import theme.YellowButtonColor
import ui.components.navigation.ScreenWithAppBar

@Composable
fun AdminScreen(navController: NavController) {
    ScreenWithAppBar("Admin Screen", { navController.navigate("dashboard") }, YellowButtonColor) {
        Text("Admin Content")
    }
}