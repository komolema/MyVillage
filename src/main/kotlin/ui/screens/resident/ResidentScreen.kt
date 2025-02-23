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
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = query.value,
                onValueChange = { query.value = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search residents...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.residents.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black)
                ) {
                    BasicPaginatedDataTable(
                        contentPadding = PaddingValues(10.dp),
                        separator = {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.Black)
                            )
                        },

                        headerBackgroundColor = Color.Gray,
                        rowBackgroundColor ={
                            if (it == clickedRow) Color.Blue else Color.LightGray
                        },
                        rowHeight = 50.dp,

                        state = dataTableState,
                        columns = listOf(
                            DataColumn(
                                width = TableColumnWidth.Flex(1f)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("ID Number")
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(200.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Name")
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Flex(100f)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Date of Birth")
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Flex(100f)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Age")
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(100.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Gender")
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(400.dp)

                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Address")
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Flex(100f)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Dependants")
                                }
                            }
                        )
                    ) {
                        state.residents.forEachIndexed { index, resExp ->
                            val backgroundColor = if (index % 2 == 0) Color.DarkGray else Color.Gray
                            row {
                                color = backgroundColor
                                onClick = {
                                    clickedRow = index
                                    navController.navigate("resident/${resExp.resident.id}?mode=view")

                                }
                                cell {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) { Text(resExp.resident.idNumber) }
                                }
                                cell {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) { Text("${resExp.resident.firstName} ${resExp.resident.lastName}") }
                                }
                                cell {
                                   Box(
                                       modifier = Modifier.fillMaxWidth(),
                                       contentAlignment = Alignment.Center
                                   ) { Text(resExp.resident.dob.format(DateTimeFormatter.ISO_DATE)) }
                                }
                                cell {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val age = Period.between(resExp.resident.dob, LocalDate.now()).years
                                        Text(age.toString())
                                    }
                                }
                                cell {
                                   Box(
                                       modifier = Modifier.fillMaxWidth(),
                                       contentAlignment = Alignment.Center
                                   ) { Text(resExp.resident.gender)}
                                }
                                cell {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) { Text(resExp.address.fold({ "" }, { it.formatFriendly() })) }
                                }
                                cell {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) { Text(resExp.dependants.size.toString()) }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Showing ${state.residents.size} of ${state.totalItems} residents")

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { dataTableState.pageIndex-- },
                        enabled = dataTableState.pageIndex > 0
                    ) {
                        Icon(Icons.Default.ArrowBack, "Previous Page")
                    }

                    Text("Page ${dataTableState.pageIndex + 1} of ${dataTableState.pageSize}")

                    IconButton(
                        onClick = { dataTableState.pageIndex++ },
                        enabled = dataTableState.pageIndex < dataTableState.pageSize - 1
                    ) {
                        Icon(Icons.Default.ArrowForward, "Next Page")
                    }
                }
            }

            Button(
                onClick = { navController.navigate("resident/?mode=new") },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add New Resident")
            }
        }
    }
}