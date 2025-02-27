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
import com.seanproctor.datatable.*
import com.seanproctor.datatable.paging.BasicPaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import models.formatFriendly
import ui.components.WindowToolbar
import ui.screens.resident.WindowMode
import ui.utils.ScrollableContainer
import viewmodel.ResidentViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

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
@OptIn(ExperimentalMaterialApi::class)
fun ResidentScreen(navController: NavController, viewModel: ResidentViewModel) {
    val state by viewModel.state.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedColumn by remember { mutableStateOf("ID Number") }
    val columns = listOf("ID Number", "First Name", "Last Name", "Date of Birth", "Age", "Gender", "Phone Number", "Email")
    val pageSize = 20
    val dataTableState = rememberPaginatedDataTableState(10, 0, 0)
    var clickedRow by remember { mutableStateOf(-1) }

    LaunchedEffect(searchText, selectedColumn, dataTableState.pageIndex) {
        if (searchText.isEmpty()) {
            viewModel.processIntent(ResidentViewModel.Intent.LoadResidents(dataTableState.pageSize))
        } else {
            val columnKey = when (selectedColumn) {
                "ID Number" -> "id"
                "First Name" -> "firstName"
                "Last Name" -> "lastName"
                "Date of Birth" -> "dob"
                "Age" -> "age"
                "Gender" -> "gender"
                "Phone Number" -> "phone"
                "Email" -> "email"
                else -> "id"
            }
            val searchQuery = "$columnKey:$searchText"
            viewModel.processIntent(ResidentViewModel.Intent.Search(searchQuery, dataTableState.pageIndex))
        }
    }

    Scaffold(
        topBar = {
            WindowToolbar(
                mode = WindowMode.VIEW,
                onToggleEdit = { /* Handle mode toggle */ },
                onSave = { /* Handle save */ },
                onClose = { navController.popBackStack() }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {


            if (state.residents.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.width(200.dp)
                    ) {
                        TextField(
                            value = selectedColumn,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            columns.forEach { column ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedColumn = column
                                        expanded = false
                                    }
                                ) {
                                    Text(column)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search...") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                val columnKey = when (selectedColumn) {
                                    "ID Number" -> "id"
                                    "First Name" -> "firstName"
                                    "Last Name" -> "lastName"
                                    "Date of Birth" -> "dob"
                                    "Age" -> "age"
                                    "Gender" -> "gender"
                                    "Phone Number" -> "phone"
                                    "Email" -> "email"
                                    else -> "id"
                                }
                                val searchQuery = "$columnKey:$searchText"
                                viewModel.processIntent(ResidentViewModel.Intent.Search(searchQuery, dataTableState.pageIndex))
                            }
                        )
                    )
                }

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
                            DataColumn(width = TableColumnWidth.Fixed(100.dp)) { HeaderCell("Gender") },
                            DataColumn(width = TableColumnWidth.Flex(1f)) { HeaderCell("Address") },
                            DataColumn(width = TableColumnWidth.Fixed(100.dp)) { HeaderCell("Dependants") },
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
                            row {
                                onClick = {
                                    clickedRow = index
                                    navController.navigate("resident/${resExp.resident.id}?mode=view")
                                }
                                cell {
                                    TableCell(resExp.resident.idNumber)
                                }
                                cell {
                                    TableCell(resExp.resident.firstName)
                                }
                                cell {
                                    TableCell(resExp.resident.lastName)
                                }
                                cell {
                                    TableCell(resExp.resident.dob.format(DateTimeFormatter.ISO_DATE))
                                }
                                cell {
                                    val age = Period.between(resExp.resident.dob, LocalDate.now()).years
                                    TableCell(age.toString())
                                }
                                cell {
                                    TableCell(resExp.resident.gender)
                                }
                                cell {
                                    TableCell(resExp.address.fold({ "" }, { it.formatFriendly() }))
                                }
                                cell {
                                    TableCell(resExp.dependants.size.toString())
                                }
                                cell {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
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
