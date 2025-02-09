package ui.screens.resident.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import models.Dependant
import ui.screens.resident.WindowMode

@Composable
fun DependantTab(dependant: Dependant?, mode: WindowMode) {
    val editableDependant = remember { mutableStateOf(dependant ?: Dependant.default) }

    Column {
        TextField(
            label = { Text("Name") },
            value = editableDependant.value.name,
            enabled = mode != WindowMode.VIEW,
            onValueChange = { editableDependant.value = editableDependant.value.copy(name = it) }
        )

        // Other fields

        if (mode == WindowMode.NEW) {
            Button(onClick = { /* Save new dependant */ }) {
                Text("Create Dependant")
            }
        }
    }
}
