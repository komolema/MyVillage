package ui.screens.resident.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.seanproctor.datatable.TableColumnWidth
import models.Dependant
import ui.components.table.GenericTable
import ui.components.table.TableCellType
import ui.components.table.TableColumn
import ui.components.table.TableConfig
import ui.screens.resident.WindowMode
import viewmodel.ResidentWindowViewModel
import viewmodel.ResidentWindowViewModel.Intent
import java.util.*
import ui.screens.resident.tabs.TabCompletionState

@Composable
fun DependentsTab(
    residentId: UUID?,
    viewModel: ResidentWindowViewModel,
    mode: WindowMode,
    onTabStateChange: (TabCompletionState) -> Unit = {}
) {
    var dependants by remember { mutableStateOf(emptyList<Dependant>()) }
    var selectedDependant by remember { mutableStateOf<Dependant?>(null) }
    var showAddForm by remember { mutableStateOf(false) }

    // Load dependants when residentId changes
    LaunchedEffect(residentId) {
        if (residentId != null) {
            viewModel.processIntent(Intent.LoadDependants(residentId))
        }
    }

    // Collect state updates
    LaunchedEffect(Unit) {
        viewModel.state.collect { state ->
            dependants = state.dependants
        }
    }

    // Update tab state based on data
    LaunchedEffect(dependants) {
        onTabStateChange(
            if (dependants.isEmpty()) TabCompletionState.TODO
            else TabCompletionState.DONE
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dependents Information",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = { showAddForm = true },
                enabled = mode == WindowMode.UPDATE
            ) {
                Text("Add Dependent")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dependants table
        GenericTable(
            items = dependants,
            config = TableConfig(
                columns = listOf(
                    TableColumn(
                        title = "ID Number",
                        width = TableColumnWidth.Fixed(150.dp),
                        getValue = { it.idNumber },
                        setValue = { dep, value -> dep.copy(idNumber = value) }
                    ),
                    TableColumn(
                        title = "Name",
                        width = TableColumnWidth.Fixed(150.dp),
                        getValue = { it.name },
                        setValue = { dep, value -> dep.copy(name = value) }
                    ),
                    TableColumn(
                        title = "Surname",
                        width = TableColumnWidth.Fixed(150.dp),
                        getValue = { it.surname },
                        setValue = { dep, value -> dep.copy(surname = value) }
                    ),
                    TableColumn(
                        title = "Gender",
                        width = TableColumnWidth.Fixed(100.dp),
                        type = TableCellType.DROPDOWN,
                        options = listOf("Male", "Female", "Other"),
                        getValue = { it.gender },
                        setValue = { dep, value -> dep.copy(gender = value) }
                    )
                ),
                isEditable = mode == WindowMode.UPDATE,
                onEdit = { selectedDependant = it },
                onDelete = { dependant ->
                    if (residentId != null) {
                        viewModel.processIntent(Intent.DeleteDependant(dependant.id))
                    }
                },
                onSave = { updatedDependant ->
                    if (residentId != null) {
                        viewModel.processIntent(Intent.UpdateDependant(updatedDependant))
                    }
                }
            ),
            modifier = Modifier.weight(1f)
        )
    }

    if (showAddForm || selectedDependant != null) {
        DependantFormDialog(
            dependant = selectedDependant,
            onDismiss = {
                showAddForm = false
                selectedDependant = null
            },
            onSave = { newDependant ->
                if (residentId != null) {
                    val dependantWithResidentId = newDependant.copy(residentId = residentId)
                    viewModel.processIntent(
                        if (selectedDependant != null)
                            Intent.UpdateDependant(dependantWithResidentId)
                        else
                            Intent.CreateDependant(dependantWithResidentId)
                    )
                }
                showAddForm = false
                selectedDependant = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DependantFormDialog(
    dependant: Dependant?,
    onDismiss: () -> Unit,
    onSave: (Dependant) -> Unit
) {
    var idNumber by remember { mutableStateOf(dependant?.idNumber ?: "") }
    var name by remember { mutableStateOf(dependant?.name ?: "") }
    var surname by remember { mutableStateOf(dependant?.surname ?: "") }
    var gender by remember { mutableStateOf(dependant?.gender ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (dependant == null) "Add Dependent" else "Edit Dependent")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = idNumber,
                    onValueChange = { idNumber = it },
                    label = { Text("ID Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Surname") },
                    modifier = Modifier.fillMaxWidth()
                )
                var expanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { 
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Toggle dropdown"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .align(Alignment.TopStart)
                    ) {
                        Dependant.VALID_GENDERS.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newDependant = (dependant ?: Dependant.default).copy(
                        idNumber = idNumber,
                        name = name,
                        surname = surname,
                        gender = gender
                    )
                    onSave(newDependant)
                },
                enabled = idNumber.isNotBlank() && name.isNotBlank() && surname.isNotBlank() && gender.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
