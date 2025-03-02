package ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.paging.BasicPaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import ui.components.DatePicker

@Composable
fun <T> GenericTable(
    items: List<T>,
    config: TableConfig<T>,
    modifier: Modifier = Modifier
) {
    var editingCell by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var editedValues by remember { mutableStateOf(mutableMapOf<Pair<Int, String>, String>()) }

    val dataTableState = rememberPaginatedDataTableState(10, 0, 0)

    BasicPaginatedDataTable(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 2000.dp),
        state = dataTableState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        columns = buildColumns(config, editingCell, editedValues) { rowIndex, columnName ->
            editingCell = rowIndex to columnName
        },
        separator = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )
        }
    ) {
        items.forEachIndexed { index, item ->
            row {
                // Render cells for each column
                config.columns.forEach { column ->
                    cell {
                        EditableCell(
                            item = item,
                            column = column,
                            rowIndex = index,
                            isEditing = editingCell?.first == index && editingCell?.second == column.title,
                            currentValue = editedValues[index to column.title] ?: column.getValue(item),
                            onValueChange = { newValue ->
                                editedValues[index to column.title] = newValue
                            },
                            onEditComplete = {
                                if (column.validation(editedValues[index to column.title] ?: "")) {
                                    config.onSave?.invoke(
                                        column.setValue(
                                            item,
                                            editedValues[index to column.title] ?: column.getValue(item)
                                        )
                                    )
                                    editingCell = null
                                    editedValues.remove(index to column.title)
                                }
                            }
                        )
                    }
                }

                // Action buttons
                if (config.isEditable) {
                    cell {
                        Row {
                            IconButton(onClick = { config.onEdit?.invoke(item) }) {
                                Icon(Icons.Default.Edit, "Edit")
                            }
                            IconButton(onClick = { config.onDelete?.invoke(item) }) {
                                Icon(Icons.Default.Delete, "Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> buildColumns(
    config: TableConfig<T>,
    editingCell: Pair<Int, String>?,
    editedValues: Map<Pair<Int, String>, String>,
    onStartEdit: (Int, String) -> Unit
): List<DataColumn> {
    return config.columns.map { column ->
        DataColumn(width = column.width) {
            Text(
                text = column.title,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(8.dp)
            )
        }
    } + if (config.isEditable) {
        listOf(
            DataColumn {
                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(8.dp)
                )
            }
        )
    } else {
        emptyList()
    }
}

@Composable
private fun <T> EditableCell(
    item: T,
    column: TableColumn<T>,
    rowIndex: Int,
    isEditing: Boolean,
    currentValue: String,
    onValueChange: (String) -> Unit,
    onEditComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        when {
            isEditing -> when (column.type) {
                TableCellType.TEXT -> TextField(
                    value = currentValue,
                    onValueChange = onValueChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onEditComplete() })
                )
                TableCellType.NUMBER -> TextField(
                    value = currentValue,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                            onValueChange(newValue)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onEditComplete() })
                )
                TableCellType.DROPDOWN -> {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        TextField(
                            value = currentValue,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, "Dropdown")
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            column.options.forEach { option ->
                                DropdownMenuItem(onClick = {
                                    onValueChange(option)
                                    expanded = false
                                    onEditComplete()
                                }) {
                                    Text(option)
                                }
                            }
                        }
                    }
                }
                TableCellType.DATE -> {
                    val date = try {
                        if (currentValue.isNotEmpty()) {
                            LocalDate.parse(currentValue, DateTimeFormatter.ISO_DATE)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }

                    DatePicker(
                        date = date,
                        onDateSelected = { selectedDate: LocalDate ->
                            onValueChange(selectedDate.format(DateTimeFormatter.ISO_DATE))
                            onEditComplete()
                        }
                    )
                }
                else -> Text(currentValue)
            }
            else -> Text(currentValue)
        }
    }
}
