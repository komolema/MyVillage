package ui.screens.resident.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import models.Resident
import ui.screens.resident.WindowMode

@Composable
fun ResidentTab(resident: Resident?, mode: WindowMode) {
    val editableResident = remember { mutableStateOf(resident ?: Resident.default) }

    Column {
        TextField(
            label = { Text("First Name") },
            value = editableResident.value.firstName,
            enabled = mode != WindowMode.VIEW,
            onValueChange = { editableResident.value = editableResident.value.copy(firstName = it) }
        )

        // Other fields

        if (mode == WindowMode.NEW) {
            Button(onClick = { /* Save new resident */ }) {
                Text("Create Resident")
            }
        }
    }
}