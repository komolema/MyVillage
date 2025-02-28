package ui.screens.resident.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.screens.resident.WindowMode
import viewmodel.ResidentWindowViewModel
import java.util.*
import ui.screens.resident.tabs.TabCompletionState

@Composable
fun ResidenceTab(
    residentId: UUID?,
    viewModel: ResidentWindowViewModel,
    mode: WindowMode,
    onTabStateChange: (TabCompletionState) -> Unit = {}
) {
    // Since this tab is not implemented yet, we'll mark it as TODO
    LaunchedEffect(Unit) {
        onTabStateChange(TabCompletionState.TODO)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Residence Information",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Implement residence details form
        Text("Residence information management will be implemented here")
    }
}
