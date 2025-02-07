package ui.screens.settings

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import theme.GrayButtonColor
import ui.components.navigation.ScreenWithAppBar

@Composable
fun SettingsScreen(navController: NavController) {
    ScreenWithAppBar("Settings Screen", { navController.navigate("dashboard") }, GrayButtonColor) {
        Text("Settings Content")
    }
}