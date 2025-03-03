package ui.screens.resident

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.paging.BasicPaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import models.expanded.ResidentExpanded
import ui.components.WindowToolbar
import ui.components.ScrollableContainer
import viewmodel.ResidentViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

// State for tracking edited cells
data class EditedCell(val rowIndex: Int, val columnName: String, val value: String)

@Composable
private fun EditableCell(
    text: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    onEditComplete: () -> Unit,
    background: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isEditing) {
            TextField(
                value = text,
                onValueChange = onValueChange,
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onEditComplete() }),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditComplete() }
            )
        }
    }
}

@Composable
private fun TableCell(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
private fun TableCellWithBackground(text: String, background: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
private fun EditableTableCell(
    initialText: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    onEditComplete: () -> Unit,
    background: Color = MaterialTheme.colors.surface,
    windowMode: WindowMode = WindowMode.VIEW
) {
    var text by remember { mutableStateOf(initialText) }
    val isEditable = windowMode == WindowMode.UPDATE

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isEditing && isEditable) {
            TextField(
                value = text,
                onValueChange = { 
                    text = it
                    onValueChange(it)
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onEditComplete() }),
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditable
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isEditable) { 
                        if (isEditable) {
                            onEditComplete()
                        }
                    }
            )
        }
    }
}

@Composable
private fun HeaderCell(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
private fun ResidentIdCell(
    index: Int,
    resExp: ResidentExpanded,
    rowBackground: Color,
    editingCell: EditedCell?,
    editedCells: MutableMap<Pair<Int, String>, String>,
    onEdit: (EditedCell) -> Unit,
    windowMode: WindowMode
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBackground)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        val isEditable = windowMode == WindowMode.UPDATE
        if (editingCell?.rowIndex == index && editingCell.columnName == "idNumber" && isEditable) {
            TextField(
                value = editedCells[index to "idNumber"] ?: resExp.resident.idNumber,
                onValueChange = { editedCells[index to "idNumber"] = it },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { 
                    onEdit(EditedCell(index, "idNumber", resExp.resident.idNumber))
                }),
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditable
            )
        } else {
            Text(
                text = editedCells[index to "idNumber"] ?: resExp.resident.idNumber,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isEditable) { 
                        if (isEditable) {
                            onEdit(EditedCell(index, "idNumber", resExp.resident.idNumber))
                        }
                    }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
internal fun ResidentScreen(navController: NavController, viewModel: ResidentViewModel) {
    val state by viewModel.state.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var windowMode by remember { mutableStateOf(WindowMode.VIEW) }
    val pageSize = 20
    val dataTableState = rememberPaginatedDataTableState(10, 0, 0)
    var clickedRow by remember { mutableStateOf(-1) }

    // State for tracking edited cells
    var editingCell by remember { mutableStateOf<EditedCell?>(null) }
    var editedCells by remember { mutableStateOf(mutableMapOf<Pair<Int, String>, String>()) }

    // Function to check if a row has been modified
    fun isRowModified(rowIndex: Int): Boolean {
        return editedCells.any { (key, _) -> key.first == rowIndex }
    }

    LaunchedEffect(dataTableState.pageIndex) {
        viewModel.processIntent(ResidentViewModel.Intent.LoadResidents(dataTableState.pageSize))
    }

    fun performSearch() {
        viewModel.processIntent(
            if (searchText.isEmpty()) ResidentViewModel.Intent.LoadResidents(dataTableState.pageSize)
            else ResidentViewModel.Intent.Search(searchText, dataTableState.pageIndex)
        )
    }

    LaunchedEffect(searchText) {
        kotlinx.coroutines.delay(500) // Add 500ms debounce
        performSearch()
    }

    Scaffold(
        topBar = {
            WindowToolbar(
                mode = windowMode,
                onToggleEdit = { 
                    windowMode = when (windowMode) {
                        WindowMode.VIEW -> WindowMode.UPDATE
                        WindowMode.UPDATE -> WindowMode.VIEW
                        else -> windowMode
                    }
                },
                onSave = { 
                    // Save changes and switch back to view mode
                    if (windowMode == WindowMode.UPDATE) {
                        // Save all modified rows
                        state.residents.forEachIndexed { index, resExp ->
                            if (isRowModified(index)) {
                                val updatedResident = resExp.resident.copy(
                                    idNumber = editedCells[index to "idNumber"] ?: resExp.resident.idNumber,
                                    firstName = editedCells[index to "firstName"] ?: resExp.resident.firstName,
                                    lastName = editedCells[index to "lastName"] ?: resExp.resident.lastName,
                                    dob = editedCells[index to "dob"]?.let { LocalDate.parse(it) } ?: resExp.resident.dob,
                                    gender = editedCells[index to "gender"] ?: resExp.resident.gender
                                )
                                viewModel.processIntent(ResidentViewModel.Intent.SaveResidentChanges(updatedResident))
                            }
                        }
                        // Clear editing state
                        editingCell = null
                        editedCells.clear()
                        windowMode = WindowMode.VIEW
                    }
                },
                onClose = { navController.popBackStack() }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search by ID number, name or other details...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { performSearch() }
                    )
                )
            }

            if (state.residents.isNotEmpty()) {

                ScrollableContainer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .padding(horizontal = 16.dp)
                        .border(1.dp, Color.LightGray, shape = MaterialTheme.shapes.small)
                ) {
                    BasicPaginatedDataTable(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 2000.dp)
                            .widthIn(max = 1200.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        separator = {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.LightGray.copy(alpha = 0.5f))
                            )
                        },
                        headerBackgroundColor = MaterialTheme.colors.surface,
                        rowBackgroundColor = { index ->
                            when {
                                index == clickedRow -> MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                index % 2 == 0 -> Color.White
                                else -> Color(0xFFF8F9FA)
                            }
                        },
                        rowHeight = 56.dp,

                        state = dataTableState,
                        columns = listOf(
                            DataColumn(width = TableColumnWidth.Fixed(150.dp)) { HeaderCell("ID Number") },
                            DataColumn(width = TableColumnWidth.Fixed(120.dp)) { HeaderCell("First Name") },
                            DataColumn(width = TableColumnWidth.Fixed(120.dp)) { HeaderCell("Last Name") },
                            DataColumn(width = TableColumnWidth.Fixed(120.dp)) { HeaderCell("Date of Birth") },
                            DataColumn(width = TableColumnWidth.Fixed(80.dp)) { HeaderCell("Age") },
                            DataColumn(width = TableColumnWidth.Fixed(150.dp)) { HeaderCell("Gender") },
                            DataColumn(width = TableColumnWidth.Flex(1f)) { HeaderCell("Address") },
                            DataColumn(width = TableColumnWidth.Fixed(100.dp)) { HeaderCell("Dependants") },
                            DataColumn(
                                width = TableColumnWidth.Fixed(80.dp)
                            ) {
                                Column {
                                    HeaderCell("Edit")
                                    Spacer(modifier = Modifier.height(40.dp))
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(80.dp)
                            ) {
                                Column {
                                    HeaderCell("Save")
                                    Spacer(modifier = Modifier.height(40.dp))
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(80.dp)
                            ) {
                                Column {
                                    HeaderCell("Reload")
                                    Spacer(modifier = Modifier.height(40.dp))
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(80.dp)
                            ) {
                                Column {
                                    HeaderCell("Delete")
                                    Spacer(modifier = Modifier.height(40.dp))
                                }
                            }
                        )
                    ) {
                        state.residents.forEachIndexed { index, resExp ->
                            val rowBackground = if (isRowModified(index)) Color.Yellow.copy(alpha = 0.2f)
                                              else Color.LightGray.copy(alpha = 0.1f)
                            row {
                                cell {
                                    ResidentIdCell(
                                        index = index,
                                        resExp = resExp,
                                        rowBackground = rowBackground,
                                        editingCell = editingCell,
                                        editedCells = editedCells,
                                        onEdit = { cell ->
                                            // Clear previous edits if switching to a different row
                                            if (editingCell?.rowIndex != cell.rowIndex) {
                                                editedCells.clear()
                                            }
                                            editingCell = cell
                                        },
                                        windowMode = windowMode
                                    )
                                }
                                cell { 
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(rowBackground)
                                            .padding(horizontal = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val isEditable = windowMode == WindowMode.UPDATE
                                        if (editingCell?.rowIndex == index && editingCell?.columnName == "firstName" && isEditable) {
                                            TextField(
                                                value = editedCells[index to "firstName"] ?: resExp.resident.firstName,
                                                onValueChange = { editedCells[index to "firstName"] = it },
                                                singleLine = true,
                                                colors = TextFieldDefaults.textFieldColors(
                                                    backgroundColor = Color.Transparent
                                                ),
                                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                                keyboardActions = KeyboardActions(onDone = { 
                                                    editingCell = null 
                                                }),
                                                modifier = Modifier.fillMaxWidth(),
                                                enabled = isEditable
                                            )
                                        } else {
                                            Text(
                                                text = editedCells[index to "firstName"] ?: resExp.resident.firstName,
                                                style = MaterialTheme.typography.body2,
                                                color = MaterialTheme.colors.onSurface,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable(enabled = isEditable) { 
                                                        if (isEditable) {
                                                            editingCell = EditedCell(index, "firstName", resExp.resident.firstName)
                                                        }
                                                    }
                                            )
                                        }
                                    }
                                }
                                cell {
                                    EditableTableCell(
                                        initialText = resExp.resident.lastName,
                                        isEditing = editingCell?.rowIndex == index && editingCell?.columnName == "lastName",
                                        onValueChange = { editedCells[index to "lastName"] = it },
                                        onEditComplete = { editingCell = EditedCell(index, "lastName", resExp.resident.lastName) },
                                        background = rowBackground,
                                        windowMode = windowMode
                                    )
                                }
                                cell {
                                    EditableTableCell(
                                        initialText = resExp.resident.dob.format(DateTimeFormatter.ISO_DATE),
                                        isEditing = editingCell?.rowIndex == index && editingCell?.columnName == "dob",
                                        onValueChange = { editedCells[index to "dob"] = it },
                                        onEditComplete = { editingCell = EditedCell(index, "dob", resExp.resident.dob.format(DateTimeFormatter.ISO_DATE)) },
                                        background = rowBackground,
                                        windowMode = windowMode
                                    )
                                }
                                cell {
                                    val age = Period.between(resExp.resident.dob, LocalDate.now()).years
                                    TableCell(age.toString()) // Age is calculated, not editable
                                }
                                cell {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(rowBackground)
                                            .padding(horizontal = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        var expanded by remember { mutableStateOf(false) }
                                        val genderOptions = listOf("Male", "Female", "Other")
                                        val currentGender = editedCells[index to "gender"] ?: resExp.resident.gender

                                        Column {
                                            OutlinedButton(
                                                onClick = { if (windowMode == WindowMode.UPDATE) expanded = true },
                                                modifier = Modifier.fillMaxWidth(),
                                                enabled = windowMode == WindowMode.UPDATE
                                            ) {
                                                Text(currentGender.ifEmpty { "Select Gender" })
                                            }

                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                                modifier = Modifier.fillMaxWidth(0.9f)
                                            ) {
                                                genderOptions.forEach { gender ->
                                                    DropdownMenuItem(
                                                        onClick = {
                                                            editedCells[index to "gender"] = gender
                                                            expanded = false
                                                        }
                                                    ) {
                                                        Text(gender)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                cell {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(rowBackground)
                                            .padding(horizontal = 8.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = resExp.address.fold(
                                                { "No address" },
                                                { it.formatFriendly() }
                                            ),
                                            style = MaterialTheme.typography.body2
                                        )
                                    }
                                }
                                cell {
                                    TableCellWithBackground(resExp.dependants.size.toString(), rowBackground) // Dependants count is read-only
                                }
                                cell {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(rowBackground),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(
                                            onClick = {
                                                navController.navigate("resident/${resExp.resident.id}?mode=update")
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = MaterialTheme.colors.primary
                                            )
                                        }
                                    }
                                }
                                cell {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(rowBackground),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (isRowModified(index)) {
                                                    // Save changes
                                                    viewModel.processIntent(
                                                        ResidentViewModel.Intent.SaveResidentChanges(
                                                            resExp.resident.copy(
                                                                idNumber = editedCells[index to "idNumber"] ?: resExp.resident.idNumber,
                                                                firstName = editedCells[index to "firstName"] ?: resExp.resident.firstName,
                                                                lastName = editedCells[index to "lastName"] ?: resExp.resident.lastName,
                                                                gender = editedCells[index to "gender"] ?: resExp.resident.gender,
                                                                dob = editedCells[index to "dob"]?.let { LocalDate.parse(it) } ?: resExp.resident.dob
                                                            )
                                                        )
                                                    )
                                                    // Clear edited cells for this row
                                                    editedCells = editedCells.filterNot { it.key.first == index }.toMutableMap()
                                                }
                                            },
                                            enabled = isRowModified(index),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Done,
                                                contentDescription = "Save",
                                                tint = if (isRowModified(index)) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                                            )
                                        }
                                    }
                                }
                                cell {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(rowBackground),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (isRowModified(index)) {
                                                    // Clear edited cells for this row
                                                    editedCells = editedCells.filterNot { it.key.first == index }.toMutableMap()
                                                    editingCell = null
                                                }
                                            },
                                            enabled = isRowModified(index),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Reload",
                                                tint = if (isRowModified(index)) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                                            )
                                        }
                                    }
                                }
                                cell {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(rowBackground),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        IconButton(
                                            onClick = {
                                                viewModel.processIntent(
                                                    ResidentViewModel.Intent.DeleteResident(resExp.resident.id)
                                                )
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colors.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Showing ${state.residents.size} of ${state.totalItems} residents",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { dataTableState.pageIndex-- },
                            enabled = dataTableState.pageIndex > 0,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, "Previous Page")
                        }

                        Text(
                            "Page ${dataTableState.pageIndex + 1} of ${dataTableState.pageSize}",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        OutlinedButton(
                            onClick = { dataTableState.pageIndex++ },
                            enabled = dataTableState.pageIndex < dataTableState.pageSize - 1,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, "Next Page")
                        }
                    }
                }
            }

            Button(
                onClick = { navController.navigate("resident/?mode=new") },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 16.dp, bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Add New Resident",
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}
