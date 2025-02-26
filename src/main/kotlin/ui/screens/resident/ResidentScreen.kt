import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun ResidentScreen(navController: NavController, viewModel: ResidentViewModel) {
    val state by viewModel.state.collectAsState()
    val query = remember { mutableStateOf("") }
    val pageSize = 20
    val dataTableState = rememberPaginatedDataTableState(10, 0, 0)
    var clickedRow by remember { mutableStateOf(-1) }

    LaunchedEffect(query.value, dataTableState.pageIndex) {
        if (query.value.isEmpty()) {
            viewModel.processIntent(ResidentViewModel.Intent.LoadResidents(dataTableState.pageSize))
        } else {
            viewModel.processIntent(ResidentViewModel.Intent.Search(query.value, dataTableState.pageIndex))
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
            OutlinedTextField(
                value = query.value,
                onValueChange = { query.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search residents...") },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )

            if (state.residents.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .border(1.dp, Color.LightGray, shape = MaterialTheme.shapes.small)
                ) {
                    BasicPaginatedDataTable(
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
                            DataColumn(
                                width = TableColumnWidth.Flex(1.2f)
                            ) {
                                HeaderCell("ID Number")
                            },
                            DataColumn(
                                width = TableColumnWidth.Flex(1.5f)
                            ) {
                                HeaderCell("Name")
                            },
                            DataColumn(
                                width = TableColumnWidth.Flex(1f)
                            ) {
                                HeaderCell("Date of Birth")
                            },
                            DataColumn(
                                width = TableColumnWidth.Flex(0.5f)
                            ) {
                                HeaderCell("Age")
                            },
                            DataColumn(
                                width = TableColumnWidth.Flex(0.8f)
                            ) {
                                HeaderCell("Gender")
                            },
                            DataColumn(
                                width = TableColumnWidth.Flex(2f)
                            ) {
                                HeaderCell("Address")
                            },
                            DataColumn(
                                width = TableColumnWidth.Flex(0.8f)
                            ) {
                                HeaderCell("Dependants")
                            },
                            DataColumn(
                                width = TableColumnWidth.Flex(0.6f)
                            ) {
                                HeaderCell("Delete")
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
                                    TableCell("${resExp.resident.firstName} ${resExp.resident.lastName}")
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
