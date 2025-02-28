package ui.screens.resident

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import models.Resident
import models.expanded.ResidentExpanded
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import viewmodel.ResidentViewModel
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import arrow.core.Option
import models.Address
import models.Residence
import ui.screens.resident.WindowMode
import ui.screens.resident.ResidentState

@Composable
fun TestResidentScreen(navController: NavController, viewModel: ResidentViewModel) {
    val state by viewModel.state.collectAsState()
    ResidentScreen(navController = navController, viewModel = viewModel)
}

@OptIn(ExperimentalMaterialApi::class)
@RunWith(JUnit4::class)
class ResidentScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mock<ResidentViewModel>()
    private val mockNavController = mock<NavController>()

    @Test
    fun `test editing state is cleared when switching rows`() {
        // Create test data
        val resident1 = Resident.default.copy(
            id = UUID.randomUUID(),
            firstName = "John",
            lastName = "Doe"
        )
        val resident2 = Resident.default.copy(
            id = UUID.randomUUID(),
            firstName = "Jane",
            lastName = "Smith"
        )
        val residents = listOf(
            ResidentExpanded(resident1, Option.fromNullable(null), Option.fromNullable(null)),
            ResidentExpanded(resident2, Option.fromNullable(null), Option.fromNullable(null))
        )

        // Mock ViewModel state
        val state = ResidentState(
            residents = residents,
            isLoading = false,
            totalItems = residents.size
        )
        whenever(mockViewModel.state).thenReturn(MutableStateFlow(state))

        // Set up the composable
        composeTestRule.setContent {
            ResidentScreen(
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        // Wait for the UI to be ready
        composeTestRule.waitForIdle()

        // Set window mode to UPDATE to enable editing
        composeTestRule
            .onNodeWithContentDescription("Toggle Edit Mode")
            .performClick()

        // Click on first row's firstName cell to start editing
        composeTestRule
            .onNodeWithText(resident1.firstName)
            .performClick()

        // Verify first row's firstName is in edit mode
        composeTestRule
            .onNode(hasSetTextAction())
            .assertExists()
            .assertIsEnabled()

        // Click on second row's firstName cell
        composeTestRule
            .onNodeWithText(resident2.firstName)
            .performClick()

        // Verify that first row's edit state is cleared (TextField should be gone)
        composeTestRule
            .onNode(hasSetTextAction() and hasText(resident1.firstName))
            .assertDoesNotExist()
    }
}
