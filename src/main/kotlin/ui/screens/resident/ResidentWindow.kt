package ui.screens.resident

import VerticalTabs
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    viewModels: ResidentWindowViewModel
) {
    val currentTab = remember { mutableStateOf(0) }

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
        Row {
            VerticalTabs(
                selectedTab = currentTab.value,
                tabs = listOf("Resident", "Qualifications", "Dependents", "Residence", "Employment"),
                onTabSelected = { currentTab.value = it }
            )

            when (currentTab.value) {
                0 -> ResidentTab(
                    resident = viewModels.residentViewModel.loadResident(residentId),
                    mode = mode
                )
                1 -> QualificationTab(
                    qualifications = viewModels.qualificationViewModel.loadQualifications(residentId),
                    mode = mode
                )
                // Other tabs
            }
        }
    }
}
