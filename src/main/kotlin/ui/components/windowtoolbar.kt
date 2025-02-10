package ui.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ui.screens.resident.WindowMode

@Composable
fun WindowToolbar(
    mode: WindowMode,
    onToggleEdit: () -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit
) {
    TopAppBar(
        title = { Text("Resident Details") },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        },
        actions = {
            if (mode != WindowMode.NEW) {
                IconButton(onClick = onToggleEdit) {
                    Icon(
                        imageVector = if (mode == WindowMode.VIEW) Icons.Default.Edit else Icons.Default.ArrowBack,
                        contentDescription = if (mode == WindowMode.VIEW) "Edit" else "Cancel Edit"
                    )
                }
            }
            if (mode == WindowMode.UPDATE || mode == WindowMode.NEW) {
                IconButton(onClick = onSave) {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
            }
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White
    )
}