package ui.screens.resident.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.TableColumnWidth
import models.domain.Employment
import ui.components.table.GenericTable
import ui.components.table.TableCellType
import ui.components.table.TableColumn
import ui.components.table.TableConfig
import ui.screens.resident.WindowMode
import viewmodel.ResidentWindowViewModel
import viewmodel.ResidentWindowViewModel.Intent
import java.util.*
import java.time.LocalDate

@Composable
fun EmploymentTab(
    residentId: UUID?,
    viewModel: ResidentWindowViewModel,
    mode: WindowMode,
    onTabStateChange: (TabCompletionState) -> Unit = {}
) {
    var employmentHistory by remember { mutableStateOf(emptyList<Employment>()) }
    var selectedEmployment by remember { mutableStateOf<Employment?>(null) }
    var showAddForm by remember { mutableStateOf(false) }

    // Load employment history when residentId changes
    LaunchedEffect(residentId) {
        if (residentId != null) {
            viewModel.processIntent(Intent.LoadEmployment(residentId))
        }
    }

    // Collect state updates
    LaunchedEffect(Unit) {
        viewModel.state.collect { state ->
            employmentHistory = state.employmentHistory
        }
    }

    // Update tab state based on data
    LaunchedEffect(employmentHistory) {
        onTabStateChange(
            if (employmentHistory.isEmpty()) TabCompletionState.TODO
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
                text = "Employment History",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = { showAddForm = true },
                enabled = mode == WindowMode.UPDATE
            ) {
                Text("Add Employment")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Employment history table
        GenericTable(
            items = employmentHistory,
            config = TableConfig(
                columns = listOf(
                    TableColumn(
                        title = "Employer",
                        width = TableColumnWidth.Fixed(150.dp),
                        getValue = { it.employer },
                        setValue = { emp, value -> emp.copy(employer = value) }
                    ),
                    TableColumn(
                        title = "Role",
                        width = TableColumnWidth.Fixed(150.dp),
                        getValue = { it.role },
                        setValue = { emp, value -> emp.copy(role = value) }
                    ),
                    TableColumn(
                        title = "Start Date",
                        width = TableColumnWidth.Fixed(120.dp),
                        type = TableCellType.DATE,
                        getValue = { it.startDate.toString() },
                        setValue = { emp, value -> emp.copy(startDate = LocalDate.parse(value)) }
                    ),
                    TableColumn(
                        title = "End Date",
                        width = TableColumnWidth.Fixed(120.dp),
                        type = TableCellType.DATE,
                        getValue = { it.endDate?.toString() ?: "" },
                        setValue = { emp, value -> 
                            emp.copy(endDate = if (value.isBlank()) null else LocalDate.parse(value))
                        }
                    )
                ),
                isEditable = mode == WindowMode.UPDATE,
                onEdit = { selectedEmployment = it },
                onDelete = { employment ->
                    viewModel.processIntent(Intent.DeleteEmployment(employment.id))
                },
                onSave = { updatedEmployment ->
                    viewModel.processIntent(Intent.UpdateEmployment(updatedEmployment))
                }
            ),
            modifier = Modifier.weight(1f)
        )
    }

    if (showAddForm || selectedEmployment != null) {
        EmploymentFormDialog(
            employment = selectedEmployment,
            onDismiss = {
                showAddForm = false
                selectedEmployment = null
            },
            onSave = { newEmployment ->
                if (residentId != null) {
                    val employmentWithResidentId = newEmployment.copy(residentId = residentId)
                    viewModel.processIntent(
                        if (selectedEmployment != null)
                            Intent.UpdateEmployment(employmentWithResidentId)
                        else
                            Intent.CreateEmployment(employmentWithResidentId)
                    )
                }
                showAddForm = false
                selectedEmployment = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmploymentFormDialog(
    employment: Employment?,
    onDismiss: () -> Unit,
    onSave: (Employment) -> Unit
) {
    var employer by remember { mutableStateOf(employment?.employer ?: "") }
    var role by remember { mutableStateOf(employment?.role ?: "") }
    var startDate by remember { mutableStateOf(employment?.startDate?.toString() ?: "") }
    var endDate by remember { mutableStateOf(employment?.endDate?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (employment == null) "Add Employment" else "Edit Employment")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = employer,
                    onValueChange = { employer = it },
                    label = { Text("Employer") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date (YYYY-MM-DD, optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newEmployment = (employment ?: Employment.default).copy(
                        employer = employer,
                        role = role,
                        startDate = LocalDate.parse(startDate),
                        endDate = if (endDate.isBlank()) null else LocalDate.parse(endDate)
                    )
                    onSave(newEmployment)
                },
                enabled = employer.isNotBlank() && role.isNotBlank() && startDate.isNotBlank()
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