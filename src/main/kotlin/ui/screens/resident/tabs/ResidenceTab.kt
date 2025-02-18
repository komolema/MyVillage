package ui.screens.resident.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import models.Residence
import ui.screens.resident.WindowMode

@Composable
fun ResidenceTab(residence: Residence?, mode: WindowMode) {
    var editableResidence by remember { mutableStateOf(Residence.default) }

    // Other fields

    if (mode == WindowMode.NEW) {
        Button(onClick = { /* Save new residence */ }) {
            Text("Create Residence")
        }
    } else if (mode == WindowMode.UPDATE) {
        Button(onClick = { /* Save updated residence */ }) {
            Text("Update Residence")
        }
    }
}