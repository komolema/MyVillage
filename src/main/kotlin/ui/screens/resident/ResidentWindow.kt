package ui.screens.resident

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import ui.components.WindowToolbar
import ui.screens.resident.tabs.QualificationTab
import ui.screens.resident.tabs.ResidentTab
import viewmodel.ResidentWindowViewModel
import java.util.*

@Composable
fun ResidentWindow(
    residentId: UUID?,
    mode: WindowMode,
    onClose: () -> Unit,
    viewModel: ResidentWindowViewModel
) {
    val currentTab = remember { mutableStateOf(0) }
    val tabTitles = listOf("Resident", "Qualifications", "Dependents", "Residence", "Employment")

    Scaffold(
        topBar = {
            WindowToolbar(
                mode = mode,
                onToggleEdit = { /* Handle mode toggle */ },
                onSave = { /* Handle save */ },
                onClose = onClose
            )
        }
    ) {
        Column {
            TabRow(
                selectedTabIndex = currentTab.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = currentTab.value == index,
                        onClick = { currentTab.value = index },
                        text = { Text(title) }
                    )
                }
            }

            when (currentTab.value) {
                0 -> ResidentTab(
                    residentId,
                    viewModel,
                    mode = mode
                )
                1 -> QualificationTab(
                    residentId,
                    viewModel,
                    mode
                )
                // Add other tabs as needed
            }
        }
    }
}