import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.seanproctor.datatable.*
import com.seanproctor.datatable.paging.BasicPaginatedDataTable
import com.seanproctor.datatable.paging.rememberPaginatedDataTableState
import models.Address
import viewmodel.ResidentViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

// ResidentScreen.kt
@Composable
fun ResidentScreen(navController: NavController, viewModel: ResidentViewModel) {
    val state by viewModel.state.collectAsState()
    val query = remember { mutableStateOf("") }
    val pageSize = 20
    val dataTableState = rememberPaginatedDataTableState(10, 0, 10)

    LaunchedEffect(query.value, dataTableState.pageIndex) {
        if (query.value.isEmpty()) {
            viewModel.processIntent(ResidentViewModel.Intent.LoadResidents(dataTableState.pageSize))
        } else {
            viewModel.processIntent(ResidentViewModel.Intent.Search(query.value, dataTableState.pageIndex))
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
        BasicPaginatedDataTable(
            state = dataTableState,
            columns = listOf(
                DataColumn { Text("ID Number") },
                DataColumn { Text("Name") },
                DataColumn { Text("DOB") },
                DataColumn { Text("Age") },
                DataColumn { Text("Gender") },
                DataColumn { Text("Address") },
                DataColumn { Text("Dependents") }
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