package ui.screens.resident.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.TableColumnWidth
import models.Qualification
import ui.components.DatePicker
import ui.components.table.GenericTable
import ui.components.table.TableCellType
import ui.components.table.TableColumn
import ui.components.table.TableConfig
import ui.screens.resident.WindowMode
import viewmodel.ResidentWindowViewModel
import viewmodel.ResidentWindowViewModel.Intent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import ui.screens.resident.tabs.TabCompletionState

@Composable
fun QualificationTab(
    residentId: UUID?,
    viewModel: ResidentWindowViewModel,
    mode: WindowMode,
    onTabStateChange: (TabCompletionState) -> Unit = {}
) {
    var qualifications by remember { mutableStateOf(emptyList<Qualification>()) }
    var selectedQualification by remember { mutableStateOf<Qualification?>(null) }
    var showAddForm by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ISO_DATE

    // Load qualifications when residentId changes
    LaunchedEffect(residentId) {
        if (residentId != null) {
            viewModel.processIntent(Intent.LoadQualifications(residentId))
        }
    }

    // Collect state updates
    LaunchedEffect(viewModel) {
        viewModel.state.collect { state ->
            qualifications = state.qualifications
            // Close dialogs when state updates (indicating successful save)
            if (showAddForm || selectedQualification != null) {
                showAddForm = false
                selectedQualification = null
            }
        }
    }

    // Update tab state based on data
    LaunchedEffect(qualifications) {
        onTabStateChange(
            if (qualifications.isEmpty()) TabCompletionState.TODO
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
                text = "Qualifications Information",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = { showAddForm = true },
                enabled = mode == WindowMode.UPDATE
            ) {
                Text("Add Qualification")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Qualifications table
        GenericTable(
            items = qualifications,
            config = TableConfig(
                columns = listOf(
                    TableColumn(
                        title = "Name",
                        width = TableColumnWidth.Fixed(150.dp),
                        getValue = { it.name },
                        setValue = { qual, value -> qual.copy(name = value) }
                    ),
                    TableColumn(
                        title = "Institution",
                        width = TableColumnWidth.Fixed(150.dp),
                        getValue = { it.institution },
                        setValue = { qual, value -> qual.copy(institution = value) }
                    ),
                    TableColumn(
                        title = "NQF Level",
                        width = TableColumnWidth.Fixed(100.dp),
                        type = TableCellType.NUMBER,
                        getValue = { it.nqfLevel.toString() },
                        setValue = { qual, value -> qual.copy(nqfLevel = value.toIntOrNull() ?: 0) }
                    ),
                    TableColumn(
                        title = "Start Date",
                        width = TableColumnWidth.Fixed(120.dp),
                        type = TableCellType.DATE,
                        getValue = { it.startDate.format(dateFormatter) },
                        setValue = { qual, value -> 
                            qual.copy(startDate = LocalDate.parse(value, dateFormatter))
                        }
                    ),
                    TableColumn(
                        title = "End Date",
                        width = TableColumnWidth.Fixed(120.dp),
                        type = TableCellType.DATE,
                        getValue = { it.endDate?.format(dateFormatter) ?: "" },
                        setValue = { qual, value -> 
                            qual.copy(endDate = if (value.isBlank()) null else LocalDate.parse(value, dateFormatter))
                        }
                    ),
                    TableColumn(
                        title = "City",
                        width = TableColumnWidth.Fixed(150.dp),
                        getValue = { it.city },
                        setValue = { qual, value -> qual.copy(city = value) }
                    )
                ),
                isEditable = mode == WindowMode.UPDATE,
                onEdit = { selectedQualification = it },
                onDelete = { qualification ->
                    if (residentId != null) {
                        // TODO: Add DeleteQualification intent and handler
                        // viewModel.processIntent(Intent.DeleteQualification(qualification.id))
                    }
                },
                onSave = { updatedQualification ->
                    if (residentId != null) {
                        viewModel.processIntent(Intent.UpdateQualification(updatedQualification))
                    }
                }
            ),
            modifier = Modifier.weight(1f)
        )
    }

    if (showAddForm || selectedQualification != null) {
        QualificationFormDialog(
            qualification = selectedQualification,
            onDismiss = {
                showAddForm = false
                selectedQualification = null
            },
            onSave = { newQualification ->
                if (residentId != null) {
                    val qualificationWithResidentId = newQualification.copy(residentId = residentId)
                    viewModel.processIntent(
                        if (selectedQualification != null)
                            Intent.UpdateQualification(qualificationWithResidentId)
                        else
                            Intent.CreateQualification(qualificationWithResidentId)
                    )
                }
                showAddForm = false
                selectedQualification = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QualificationFormDialog(
    qualification: Qualification?,
    onDismiss: () -> Unit,
    onSave: (Qualification) -> Unit
) {
    var name by remember { mutableStateOf(qualification?.name ?: "") }
    var institution by remember { mutableStateOf(qualification?.institution ?: "") }
    var nqfLevel by remember { mutableStateOf(qualification?.nqfLevel?.toString() ?: "") }
    var startDate by remember { mutableStateOf(qualification?.startDate ?: LocalDate.now()) }
    var endDate by remember { mutableStateOf(qualification?.endDate) }
    var city by remember { mutableStateOf(qualification?.city ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (qualification == null) "Add Qualification" else "Edit Qualification")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it },
                    label = { Text("Institution") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nqfLevel,
                    onValueChange = { 
                        if (it.isEmpty() || it.toIntOrNull() != null) {
                            nqfLevel = it
                        }
                    },
                    label = { Text("NQF Level") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Start Date")
                DatePicker(
                    date = startDate,
                    onDateSelected = { startDate = it }
                )
                Text("End Date (Optional)")
                DatePicker(
                    date = endDate,
                    onDateSelected = { endDate = it }
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newQualification = (qualification ?: Qualification.default).copy(
                        name = name,
                        institution = institution,
                        nqfLevel = nqfLevel.toIntOrNull() ?: 0,
                        startDate = startDate,
                        endDate = endDate,
                        city = city
                    )
                    onSave(newQualification)
                },
                enabled = name.isNotBlank() && institution.isNotBlank() && 
                         nqfLevel.isNotBlank() && city.isNotBlank()
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
