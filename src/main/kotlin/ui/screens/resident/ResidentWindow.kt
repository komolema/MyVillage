package ui.screens.resident

import ui.screens.resident.tabs.ResidentTab
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

import ui.screens.resident.tabs.TabCompletionState
import ui.screens.resident.tabs.TabState
import models.domain.Resident
import localization.StringResourcesManager
import localization.LocaleManager

internal fun isTabDisabled(index: Int, mode: WindowMode, viewModel: ResidentWindowViewModel): Boolean {
    return when {
        mode == WindowMode.NEW && index > 0 && viewModel.state.value.resident == Resident.default -> true
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
    // Load resident data when window opens
    LaunchedEffect(residentId) {
        if (residentId != null) {
            viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))
        }
    }

    // Close window when save is successful
    LaunchedEffect(viewModel.state.value.saveSuccess) {
        if (viewModel.state.value.saveSuccess) {
            onClose()
        }
    }

    val strings = remember { mutableStateOf(StringResourcesManager.getCurrentStringResources()) }
    val currentTab = remember { mutableStateOf(0) }

    // Update strings when locale changes
    LaunchedEffect(LocaleManager.getCurrentLocale()) {
        strings.value = StringResourcesManager.getCurrentStringResources()
    }
    val tabTitles = listOf(
        strings.value.resident,
        strings.value.qualifications,
        strings.value.dependents,
        strings.value.residence,
        strings.value.employment
    )
    val tabStates = remember { mutableStateOf(List(tabTitles.size) { TabState() }) }

    fun getTabColor(state: TabCompletionState): androidx.compose.ui.graphics.Color {
        return when (state) {
            TabCompletionState.TODO -> androidx.compose.ui.graphics.Color(0xFFFF6B6B)  // Soft red
            TabCompletionState.IN_PROGRESS -> androidx.compose.ui.graphics.Color(0xFFFFD93D)  // Soft yellow
            TabCompletionState.DONE -> androidx.compose.ui.graphics.Color(0xFF6BCB77)  // Soft green
        }
    }

    fun updateTabState(index: Int, newState: TabCompletionState) {
        tabStates.value = tabStates.value.toMutableList().apply {
            set(index, TabState(newState))
        }
    }

    LaunchedEffect(viewModel.state.value.saveSuccess) {
        if (viewModel.state.value.saveSuccess) {
            onClose()
        }
    }

    Scaffold(
        topBar = {
            WindowToolbar(
                mode = viewModel.state.value.mode,
                onToggleEdit = { 
                    viewModel.processIntent(ResidentWindowViewModel.Intent.ToggleMode)
                },
                onSave = { 
                    val currentState = viewModel.state.value
                    when (currentTab.value) {
                        0 -> { // Resident tab
                            val currentResident = currentState.resident
                            if (currentResident != Resident.default) {
                                viewModel.processIntent(
                                    if (currentState.mode == WindowMode.NEW) {
                                        ResidentWindowViewModel.Intent.CreateResident(currentResident)
                                    } else {
                                        ResidentWindowViewModel.Intent.UpdateResident(currentResident)
                                    }
                                )
                            }
                        }
                        3 -> { // Residence tab
                            val currentResidence = currentState.residence
                            val currentAddress = currentState.address
                            if (currentResidence != null && currentAddress != null) {
                                viewModel.processIntent(
                                    if (currentState.mode == WindowMode.NEW) {
                                        ResidentWindowViewModel.Intent.CreateResidence(currentResidence, currentAddress)
                                    } else {
                                        ResidentWindowViewModel.Intent.UpdateResidence(currentResidence, currentAddress)
                                    }
                                )
                            }
                        }
                    }
                },
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

            // Status messages
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                viewModel.state.value.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                if (viewModel.state.value.saveSuccess) {
                    Text(
                        text = strings.value.changesSaved,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // Middle section - Tabs and content
            Column(modifier = Modifier.weight(1f)) {
                TabRow(
                    selectedTabIndex = currentTab.value,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    indicator = { tabPositions ->
                        if (currentTab.value < tabPositions.size) {
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab.value]),
                                color = getTabColor(tabStates.value[currentTab.value].completionState)
                            )
                        }
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = currentTab.value == index,
                            onClick = { if (!isTabDisabled(index, mode, viewModel)) currentTab.value = index },
                            enabled = !isTabDisabled(index, mode, viewModel),
                            modifier = Modifier.background(
                                color = getTabColor(tabStates.value[index].completionState).copy(
                                    alpha = when {
                                        isTabDisabled(index, mode, viewModel) -> 0.12f
                                        currentTab.value == index -> 1f
                                        else -> 0.6f
                                    }
                                )
                            ),
                            text = { 
                                Text(
                                    text = title,
                                    color = if (isTabDisabled(index, mode, viewModel))
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    else
                                        MaterialTheme.colorScheme.surface
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
                            mode = mode,
                            onTabStateChange = { state ->
                                tabStates.value = tabStates.value.toMutableList().apply {
                                    set(0, TabState(state))
                                }
                            }
                        )
                        1 -> QualificationTab(
                            residentId,
                            viewModel,
                            mode,
                            onTabStateChange = { state ->
                                tabStates.value = tabStates.value.toMutableList().apply {
                                    set(1, TabState(state))
                                }
                            }
                        )
                        2 -> DependentsTab(
                            residentId,
                            viewModel,
                            mode,
                            onTabStateChange = { state ->
                                tabStates.value = tabStates.value.toMutableList().apply {
                                    set(2, TabState(state))
                                }
                            }
                        )
                        3 -> ResidenceTab(
                            residentId,
                            viewModel,
                            mode,
                            onTabStateChange = { state ->
                                tabStates.value = tabStates.value.toMutableList().apply {
                                    set(3, TabState(state))
                                }
                            }
                        )
                        4 -> EmploymentTab(
                            residentId,
                            viewModel,
                            mode,
                            onTabStateChange = { state ->
                                tabStates.value = tabStates.value.toMutableList().apply {
                                    set(4, TabState(state))
                                }
                            }
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
                        Icon(Icons.Default.Close, contentDescription = strings.value.contentDescCancel)
                        Spacer(Modifier.width(4.dp))
                        Text(strings.value.cancel)
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
                            Icon(Icons.Default.Add, contentDescription = strings.value.contentDescSave)
                            Spacer(Modifier.width(4.dp))
                            Text(strings.value.save)
                        }
                    }
                }

                // Right side buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    if (currentTab.value > 0 && !isTabDisabled(currentTab.value, mode, viewModel)) {
                        Button(
                            onClick = { currentTab.value-- },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.value.contentDescPrevious)
                            Spacer(Modifier.width(4.dp))
                            Text(strings.value.previous)
                        }
                    }

                    if (currentTab.value < tabTitles.size - 1 && !isTabDisabled(currentTab.value + 1, mode, viewModel)) {
                        Button(
                            onClick = { currentTab.value++ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(strings.value.next)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = strings.value.contentDescNext)
                        }
                    }
                }
            }
        }
    }
}
