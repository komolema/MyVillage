package ui.screens.resident

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import theme.BlueButtonColor
import ui.components.navigation.ScreenWithAppBar

@Composable
fun ResidentScreen(navController: NavController, viewModel: ResidentViewModel) {
    val state by viewModel.state.collectAsState()

    Column {
        SearchBar(onSearch = { query -> viewModel.processIntent(Intent.Search(query, 0)) })

        DataTable(
            columns = listOf(
                Column("ID Number", 150),
                Column("Name", 200),
                Column("DOB", 100),
                Column("Age", 50),
                Column("Gender", 80),
                Column("Address", 250),
                Column("Dependents", 80)
            ),
            items = state.residents,
            onRowDoubleClick = { resident ->
                navController.navigate("resident/${resident.id}?mode=view")
            }
        ) { resident ->
            listOf(
                resident.idNumber,
                "${resident.firstName} ${resident.lastName}",
                resident.dob.format(),
                resident.age(),
                resident.gender,
                resident.address?.formatFriendly() ?: "",
                resident.dependents.size.toString()
            )
        }

        PaginationControls(
            currentPage = state.currentPage,
            onNext = { viewModel.processIntent(Intent.LoadResidents(state.currentPage + 1)) },
            onPrev = { viewModel.processIntent(Intent.LoadResidents(state.currentPage - 1)) }
        )

        Button(onClick = { viewModel.processIntent(Intent.AddResident) }) {
            Text("Add New Resident")
        }
    }
}