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
import java.time.LocalDate
import java.util.*

@Composable
fun ResidentTab(residentId: UUID?, viewModel: ResidentWindowViewModel, mode: WindowMode) {
    var residentState by remember { mutableStateOf(Resident.default) }
    val viewModelState by viewModel.state.collectAsStateWithLifecycle()

    // Load resident data once if in edit/view mode
    LaunchedEffect(residentId) {
        if (mode != WindowMode.NEW && residentId != null) {
            viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))
        }
    }

    // Sync UI state with ViewModel state
    LaunchedEffect(viewModelState.resident) {
        if (mode != WindowMode.NEW) {
            residentState = viewModelState.resident
        }
    }

    Column {
        TextField(
            label = { Text("First Name") },
            value = residentState.firstName,
            enabled = mode != WindowMode.VIEW,
            onValueChange = { residentState = residentState.copy(firstName = it) }
        )

        TextField(
            label = { Text("Last Name") },
            value = residentState.lastName,
            enabled = mode != WindowMode.VIEW,
            onValueChange = { residentState = residentState.copy(lastName = it) }
        )

        TextField(
            label = { Text("Date of Birth (YYYY-MM-DD)") },
            value = residentState.dob.toString(),
            enabled = mode != WindowMode.VIEW,
            onValueChange = { newDate ->
                residentState = residentState.copy(dob = LocalDate.parse(newDate))
            }
        )

        TextField(
            label = { Text("Gender") },
            value = residentState.gender,
            enabled = mode != WindowMode.VIEW,
            onValueChange = { residentState = residentState.copy(gender = it) }
        )

        TextField(
            label = { Text("ID Number") },
            value = residentState.idNumber,
            enabled = mode != WindowMode.VIEW,
            onValueChange = { residentState = residentState.copy(idNumber = it) }
        )

        TextField(
            label = { Text("Phone Number") },
            value = residentState.phoneNumber ?: "",
            enabled = mode != WindowMode.VIEW,
            onValueChange = { residentState = residentState.copy(phoneNumber = it.ifEmpty { null }) }
        )

        TextField(
            label = { Text("Email") },
            value = residentState.email ?: "",
            enabled = mode != WindowMode.VIEW,
            onValueChange = { residentState = residentState.copy(email = it.ifEmpty { null }) }
        )

        // Save Button (New or Edit)
        if (mode != WindowMode.VIEW) {
            Button(onClick = {
                if (mode == WindowMode.NEW) {
                    viewModel.processIntent(ResidentWindowViewModel.Intent.CreateResident(residentState))
                } else {
                    viewModel.processIntent(ResidentWindowViewModel.Intent.UpdateResident(residentState))
                }
            }) {
                Text(if (mode == WindowMode.NEW) "Create Resident" else "Save Changes")
            }
        }
    }
}