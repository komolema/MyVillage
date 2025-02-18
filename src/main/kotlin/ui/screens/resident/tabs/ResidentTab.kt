package ui.screens.resident.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import models.Resident
import ui.screens.resident.WindowMode
import viewmodel.ResidentWindowViewModel
import java.util.*

@Composable
fun ResidentTab(residentId: UUID?, viewModel: ResidentWindowViewModel, mode: WindowMode) {
    var residentState = viewModel.state.collectAsStateWithLifecycle().value.resident

    if (mode != WindowMode.NEW && residentId != null) {
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId!!))
    }
    Column {
        TextField(
            label = { Text("First Name") },
            value = residentState.firstName,
            enabled = mode != WindowMode.VIEW,
            onValueChange = { residentState = residentState.copy(firstName = it) }
        )

        // Other fields

        if (mode == WindowMode.NEW) {
            Button(onClick = { /* Save new resident */ }) {
                Text("Create Resident")
            }
        }
    }
}