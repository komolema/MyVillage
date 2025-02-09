package ui.screens.resident.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import models.Residence
import ui.screens.resident.WindowMode

@Composable
fun ResidenceTab(residence: Residence?, mode: WindowMode) {
    val editableResidence = remember { mutableStateOf(residence ?: Residence.default) }

    Column {
        TextField(
            label = { Text("Address") },
            value = editableResidence.value.address,
            enabled = mode != WindowMode.VIEW,
            onValueChange = { editableResidence.value = editableResidence.value.copy(address = it) }
        )

        // Other fields

        if (mode == WindowMode.NEW) {
            Button(onClick = { /* Save new residence */ }) {
                Text("Create Residence")
            }
        }
    }
}