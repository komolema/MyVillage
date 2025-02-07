package ui.screens.animal

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import theme.GreenButtonColor
import ui.components.navigation.ScreenWithAppBar

@Composable
fun AnimalScreen(navController: NavController) {
    ScreenWithAppBar("Animal Screen", { navController.navigate("dashboard") }, GreenButtonColor) {
        Text("Animal Content")
    }
}