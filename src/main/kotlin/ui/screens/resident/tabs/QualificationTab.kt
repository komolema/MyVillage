package ui.screens.resident.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.paging.BasicPaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import models.Qualification
import ui.screens.resident.WindowMode
import viewmodel.ResidentWindowViewModel
import java.util.*

@Composable
fun QualificationTab(residentId: UUID?, viewModel: ResidentWindowViewModel, mode: WindowMode) {
    val qualifications = viewModel.state.collectAsStateWithLifecycle().value.qualifications
    val editableQualification = remember { mutableStateOf(Qualification.default) }
    val dataTableState = rememberPaginatedDataTableState(10, 0, 10)

    if(mode != WindowMode.NEW && residentId != null) {
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadQualifications(residentId!!))
    }

    Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            TextField(
                label = { Text("Qualification") },
                value = editableQualification.value.name,
                enabled = mode != WindowMode.VIEW,
                onValueChange = { editableQualification.value = editableQualification.value.copy(name = it) }
            )

            // Add other fields as needed

            if (mode == WindowMode.NEW) {
                Button(onClick = {
                    viewModel.processIntent(ResidentWindowViewModel.Intent.CreateQualification(editableQualification.value))
                }) {
                    Text("Create Qualification")
                }
            } else if (mode == WindowMode.UPDATE) {
                Button(onClick = {
                    viewModel.processIntent(ResidentWindowViewModel.Intent.UpdateQualification(editableQualification.value))
                }) {
                    Text("Update Qualification")
                }
            }
        }

        BasicPaginatedDataTable(
            state = dataTableState,
            columns = listOf(
                DataColumn { Text("Name") },
                DataColumn { Text("Institution") },
                DataColumn { Text("Start Date") },
                DataColumn { Text("End Date") },
                DataColumn { Text("NQF Level") },
                DataColumn { Text("City") }
            )
        ) {
            qualifications.forEach { qualification ->
                row {
                    onClick = {
                        editableQualification.value = qualification
                    }
                    cell { Text(qualification.name) }
                    cell { Text(qualification.institution) }
                    cell { Text(qualification.startDate.toString()) }
                    cell { Text(qualification.endDate.toString()) }
                    cell { Text(qualification.nqfLevel.toString()) }
                    cell { Text(qualification.city) }
                }
            }
        }
    }
}