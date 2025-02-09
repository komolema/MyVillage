import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.seanproctor.datatable.rememberDataTableState
import models.Address
import viewmodel.ResidentViewModel

// ResidentScreen.kt
@Composable
fun ResidentScreen(navController: NavController, viewModel: ResidentViewModel) {
    val state by viewModel.state.collectAsState()
    val query = remember { mutableStateOf("") }
    val pageSize = 20
    val dataTableState = rememberDataTableState()

    LaunchedEffect(query.value, dataTableState.page) {
        if (query.value.isEmpty()) {
            viewModel.processIntent(ResidentViewModel.Intent.LoadResidents(dataTableState.page))
        } else {
            viewModel.processIntent(ResidentViewModel.Intent.Search(query.value, dataTableState.page))
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search Bar Implementation
        OutlinedTextField(
            value = query.value,
            onValueChange = { query.value = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search residents...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Data Table Implementation
        DataTable(
            state = dataTableState.copy(
                pageCount = ceil(state.totalItems.toDouble() / pageSize).toInt()
            ),
            columns = listOf(
                ColumnConfig("ID Number", 150.dp),
                ColumnConfig("Name", 200.dp),
                ColumnConfig("DOB", 120.dp),
                ColumnConfig("Age", 80.dp),
                ColumnConfig("Gender", 100.dp),
                ColumnConfig("Address", 250.dp),
                ColumnConfig("Dependents", 120.dp)
            )
        ) {
            state.residents.forEach { resident ->
                row(
                    onClick = { /* Handle single click */ },
                    onDoubleClick = {
                        navController.navigate("resident/${resident.id}?mode=view")
                    }
                ) {
                    cell { Text(resident.idNumber) }
                    cell { Text("${resident.firstName} ${resident.lastName}") }
                    cell { Text(resident.dob.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))) }
                    cell { Text(Period.between(resident.dob, LocalDate.now()).years.toString()) }
                    cell { Text(resident.gender) }
                    cell { Text(resident.address?.formatFriendly() ?: "") }
                    cell { Text(resident.dependents.size.toString()) }
                }
            }
        }

        // Pagination Controls
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Showing ${state.residents.size} of ${state.totalItems} residents")

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { dataTableState.page-- },
                    enabled = dataTableState.page > 0
                ) {
                    Icon(Icons.Default.ArrowBack, "Previous Page")
                }

                Text("Page ${dataTableState.page + 1} of ${dataTableState.pageCount}")

                IconButton(
                    onClick = { dataTableState.page++ },
                    enabled = dataTableState.page < dataTableState.pageCount - 1
                ) {
                    Icon(Icons.Default.ArrowForward, "Next Page")
                }
            }
        }

        Button(
            onClick = { navController.navigate("resident/new?mode=new") },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add New Resident")
        }
    }
}

// Address extension for friendly format
fun Address.formatFriendly(): String {
    return """
        $houseNumber $line
        $suburb
        $town
        $postalCode
    """.trimIndent()
}