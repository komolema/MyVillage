package ui.screens.resident.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import database.schema.Qualifications
import models.Qualification
import ui.screens.resident.WindowMode

@Composable
fun QualificationTab(qualifications: List<Qualifications>, mode: WindowMode) {
    val editableQualifications = remember { mutableStateOf(qualifications }

    Column {
        TextField(
            label = { Text("Qualification") },
            value = editableQualification.value.name,
            enabled = mode != WindowMode.VIEW,
            onValueChange = { editableQualification.value = editableQualification.value.copy(name = it) }
        )

        // Other fields

        if (mode == WindowMode.NEW) {
            Button(onClick = { /* Save new qualification */ }) {
                Text("Create Qualification")
            }
        }
    }
}
