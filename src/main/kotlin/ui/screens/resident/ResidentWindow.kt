package ui.screens.resident

import ResidentTab
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.TabRow


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.WindowToolbar
import ui.screens.resident.tabs.QualificationTab
import ui.screens.resident.tabs.DependentsTab
import ui.screens.resident.tabs.ResidenceTab
import ui.screens.resident.tabs.EmploymentTab
import viewmodel.ResidentWindowViewModel
import java.util.*

private fun isTabDisabled(index: Int, mode: WindowMode, viewModel: ResidentWindowViewModel): Boolean {
    return when {
        mode == WindowMode.NEW && index > 0 -> true
        viewModel.state.value.resident == null && index > 0 -> true
        else -> false
    }
}

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
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section - Empty space for visual separation
            Spacer(modifier = Modifier.height(50.dp))
            Divider(thickness = 1.dp)

            // Middle section - Tabs and content
            Column(modifier = Modifier.weight(1f)) {
                TabRow(
                    selectedTabIndex = currentTab.value,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = currentTab.value == index,
                            onClick = { if (!isTabDisabled(index, mode, viewModel)) currentTab.value = index },
                            enabled = !isTabDisabled(index, mode, viewModel),
                            text = { 
                                Text(
                                    text = title,
                                    color = if (isTabDisabled(index, mode, viewModel)) 
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
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
                        2 -> DependentsTab(
                            residentId,
                            viewModel,
                            mode
                        )
                        3 -> ResidenceTab(
                            residentId,
                            viewModel,
                            mode
                        )
                        4 -> EmploymentTab(
                            residentId,
                            viewModel,
                            mode
                        )
                    }
                }
            }

            // Bottom section - Action buttons
            Divider(thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                        Spacer(Modifier.width(4.dp))
                        Text("Cancel")
                    }

                    if (mode != WindowMode.VIEW) {
                        Button(
                            onClick = { 
                                viewModel.processIntent(
                                    ResidentWindowViewModel.Intent.UpdateResident(
                                        residentState = viewModel.state.value.resident
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Save")
                            Spacer(Modifier.width(4.dp))
                            Text("Save")
                        }
                    }
                }

                // Right side buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (mode == WindowMode.NEW) {
                        Button(
                            onClick = {
                                viewModel.processIntent(
                                    ResidentWindowViewModel.Intent.CreateResident(
                                        residentState = viewModel.state.value.resident
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Create Resident")
                        }
                    }

                    if (currentTab.value > 0 && !isTabDisabled(currentTab.value, mode, viewModel)) {
                        Button(
                            onClick = { currentTab.value-- },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                            Spacer(Modifier.width(4.dp))
                            Text("Previous")
                        }
                    }

                    if (currentTab.value < tabTitles.size - 1 && !isTabDisabled(currentTab.value + 1, mode, viewModel)) {
                        Button(
                            onClick = { currentTab.value++ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Next")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                        }
                    }
                }
            }
        }
    }
}
