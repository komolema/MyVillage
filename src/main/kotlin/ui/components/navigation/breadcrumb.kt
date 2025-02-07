package ui.components.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BreadcrumbNavigation(currentScreen: String, onNavigateToDashboard: () -> Unit) {
    Row(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Dashboard",
            modifier = Modifier.clickable(onClick = onNavigateToDashboard)
        )
        Text(" > $currentScreen", color = MaterialTheme.colors.primary)
    }
}