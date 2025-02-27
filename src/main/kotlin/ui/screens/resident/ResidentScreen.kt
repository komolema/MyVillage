import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.TextFieldDefaults
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
fun ResidentScreen(navController: NavController, viewModel: ResidentViewModel) {
    val state by viewModel.state.collectAsState()
    val idNumberSearch = remember { mutableStateOf("") }
    val firstNameSearch = remember { mutableStateOf("") }
    val lastNameSearch = remember { mutableStateOf("") }
    val dobSearch = remember { mutableStateOf("") }
    val ageSearch = remember { mutableStateOf("") }
    val genderSearch = remember { mutableStateOf("") }
    val addressSearch = remember { mutableStateOf("") }
    val dependantsSearch = remember { mutableStateOf("") }
    val pageSize = 20
    val dataTableState = rememberPaginatedDataTableState(10, 0, 0)
    var clickedRow by remember { mutableStateOf(-1) }

    LaunchedEffect(
        idNumberSearch.value,
        firstNameSearch.value,
        lastNameSearch.value,
        dobSearch.value,
        ageSearch.value,
        genderSearch.value,
        addressSearch.value,
        dependantsSearch.value,
        dataTableState.pageIndex
    ) {
        val hasActiveSearch = listOf(
            idNumberSearch.value,
            firstNameSearch.value,
            lastNameSearch.value,
            dobSearch.value,
            ageSearch.value,
            genderSearch.value,
            addressSearch.value,
            dependantsSearch.value
        ).any { it.isNotEmpty() }

        if (!hasActiveSearch) {
            viewModel.processIntent(ResidentViewModel.Intent.LoadResidents(dataTableState.pageSize))
        } else {
            val searchQuery = buildString {
                if (idNumberSearch.value.isNotEmpty()) append("id:${idNumberSearch.value} ")
                if (firstNameSearch.value.isNotEmpty()) append("firstName:${firstNameSearch.value} ")
                if (lastNameSearch.value.isNotEmpty()) append("lastName:${lastNameSearch.value} ")
                if (dobSearch.value.isNotEmpty()) append("dob:${dobSearch.value} ")
                if (ageSearch.value.isNotEmpty()) append("age:${ageSearch.value} ")
                if (genderSearch.value.isNotEmpty()) append("gender:${genderSearch.value} ")
                if (addressSearch.value.isNotEmpty()) append("address:${addressSearch.value} ")
                if (dependantsSearch.value.isNotEmpty()) append("dependants:${dependantsSearch.value} ")
            }.trim()

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
                ScrollableContainer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .padding(horizontal = 16.dp)
                        .border(1.dp, Color.LightGray, shape = MaterialTheme.shapes.small)
                ) {
                    BasicPaginatedDataTable(
                        modifier = Modifier.fillMaxSize(),
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
                                width = TableColumnWidth.Fixed(150.dp)
                            ) {
                                Column {
                                    HeaderCell("ID Number")
                                    OutlinedTextField(
                                        value = idNumberSearch.value,
                                        onValueChange = { idNumberSearch.value = it },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            textColor = MaterialTheme.colors.onSurface,
                                            placeholderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                        ),
                                        placeholder = { Text("Search...") }
                                    )
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(120.dp)
                            ) {
                                Column {
                                    HeaderCell("First Name")
                                    OutlinedTextField(
                                        value = firstNameSearch.value,
                                        onValueChange = { firstNameSearch.value = it },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            textColor = MaterialTheme.colors.onSurface,
                                            placeholderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                        ),
                                        placeholder = { Text("Search...") }
                                    )
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(120.dp)
                            ) {
                                Column {
                                    HeaderCell("Last Name")
                                    OutlinedTextField(
                                        value = lastNameSearch.value,
                                        onValueChange = { lastNameSearch.value = it },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            textColor = MaterialTheme.colors.onSurface,
                                            placeholderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                        ),
                                        placeholder = { Text("Search...") }
                                    )
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(120.dp)
                            ) {
                                Column {
                                    HeaderCell("Date of Birth")
                                    OutlinedTextField(
                                        value = dobSearch.value,
                                        onValueChange = { dobSearch.value = it },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            textColor = MaterialTheme.colors.onSurface,
                                            placeholderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                        ),
                                        placeholder = { Text("Search...") }
                                    )
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(80.dp)
                            ) {
                                Column {
                                    HeaderCell("Age")
                                    OutlinedTextField(
                                        value = ageSearch.value,
                                        onValueChange = { ageSearch.value = it },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            textColor = MaterialTheme.colors.onSurface,
                                            placeholderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                        ),
                                        placeholder = { Text("Search...") }
                                    )
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(100.dp)
                            ) {
                                Column {
                                    HeaderCell("Gender")
                                    OutlinedTextField(
                                        value = genderSearch.value,
                                        onValueChange = { genderSearch.value = it },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            textColor = MaterialTheme.colors.onSurface,
                                            placeholderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                        ),
                                        placeholder = { Text("Search...") }
                                    )
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Flex(1f)
                            ) {
                                Column {
                                    HeaderCell("Address")
                                    OutlinedTextField(
                                        value = addressSearch.value,
                                        onValueChange = { addressSearch.value = it },
                                        modifier = Modifier.fillMaxWidth(0.5f).height(40.dp),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            textColor = MaterialTheme.colors.onSurface,
                                            placeholderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                        ),
                                        placeholder = { Text("Search...") }
                                    )
                                }
                            },
                            DataColumn(
                                width = TableColumnWidth.Fixed(100.dp)
                            ) {
                                Column {
                                    HeaderCell("Dependants")
                                    OutlinedTextField(
                                        value = dependantsSearch.value,
                                        onValueChange = { dependantsSearch.value = it },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            textColor = MaterialTheme.colors.onSurface,
                                            placeholderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                        ),
                                        placeholder = { Text("Search...") }
                                    )
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
