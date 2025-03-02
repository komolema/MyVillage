package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DatePicker(
    date: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Row(
        modifier = modifier
            .clickable(enabled = enabled) { showDialog = true }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date?.format(formatter) ?: "Select date",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Select date"
        )
    }

    if (showDialog) {
        DatePickerDialog(
            onDismiss = { showDialog = false },
            onDateSelected = {
                onDateSelected(it)
                showDialog = false
            },
            initialDate = date ?: LocalDate.now()
        )
    }
}

@Composable
private fun DatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate
) {
    var selectedYear by remember { mutableStateOf(initialDate.year) }
    var selectedMonth by remember { mutableStateOf(initialDate.monthValue) }
    var selectedDay by remember { mutableStateOf(initialDate.dayOfMonth) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date") },
        text = {
            Column {
                // Year selection
                OutlinedTextField(
                    value = selectedYear.toString(),
                    onValueChange = { 
                        val year = it.toIntOrNull()
                        if (year != null && year in 1900..2100) {
                            selectedYear = year
                        }
                    },
                    label = { Text("Year") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Month selection
                OutlinedTextField(
                    value = selectedMonth.toString(),
                    onValueChange = { 
                        val month = it.toIntOrNull()
                        if (month != null && month in 1..12) {
                            selectedMonth = month
                        }
                    },
                    label = { Text("Month") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Day selection
                OutlinedTextField(
                    value = selectedDay.toString(),
                    onValueChange = { 
                        val day = it.toIntOrNull()
                        if (day != null && day in 1..31) {
                            selectedDay = day
                        }
                    },
                    label = { Text("Day") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val date = LocalDate.of(selectedYear, selectedMonth, selectedDay)
                        onDateSelected(date)
                    } catch (e: Exception) {
                        // Invalid date, do nothing
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}