package ui.screens.resident.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import models.Employment
import ui.screens.resident.WindowMode

@Composable
fun EmploymentTab(employment: Employment?, mode: WindowMode) {
    val editableEmployment = remember { mutableStateOf(employment ?: Employment.default) }

    Column {
        TextField(
            label = { Text("Employer") },
            value = editableEmployment.value.employer,
            enabled = mode != WindowMode.VIEW,
            onValueChange = { editableEmployment.value = editableEmployment.value.copy(employer = it) }
        )

        // Other fields

        if (mode == WindowMode.NEW) {
            Button(onClick = { /* Save new employment */ }) {
                Text("Create Employment")
            }
        }
    }
}
