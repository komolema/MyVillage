package ui.screens.resident.tabs

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import ui.screens.resident.WindowMode
import ui.screens.resident.ResidentWindowState
import viewmodel.ResidentWindowViewModel
import java.util.*
import models.Resident
import java.time.LocalDate
import io.mockk.mockk
import io.mockk.every
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ui.screens.resident.tabs.Gender

class ResidentTabTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel = mockk<ResidentWindowViewModel>()
    private val mockState = MutableStateFlow(ResidentWindowState())

    @Test
    fun `gender field displays correctly in view mode`() {
        // Given
        val testResident = Resident.default.copy(
            gender = Gender.MALE.displayName
        )
        every { mockViewModel.state } returns mockState
        mockState.value = ResidentWindowState(resident = testResident)

        // When
        composeTestRule.setContent {
            ResidentTab(
                residentId = UUID.randomUUID(),
                viewModel = mockViewModel,
                mode = WindowMode.VIEW
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Gender:")
            .assertExists()

        composeTestRule
            .onNode(hasText(Gender.MALE.displayName))
            .assertExists()
            .assertIsNotEnabled()
    }

    @Test
    fun `gender dropdown works correctly in edit mode`() {
        // Given
        val testResident = Resident.default.copy(
            gender = ""
        )
        every { mockViewModel.state } returns mockState
        mockState.value = ResidentWindowState(resident = testResident)

        // When
        composeTestRule.setContent {
            ResidentTab(
                residentId = UUID.randomUUID(),
                viewModel = mockViewModel,
                mode = WindowMode.UPDATE
            )
        }

        // Then
        // Verify dropdown is initially closed
        composeTestRule
            .onNodeWithText("Select gender")
            .assertExists()
            .assertIsEnabled()

        // Open dropdown
        composeTestRule
            .onNodeWithText("Select gender")
            .performClick()

        // Verify dropdown options are displayed
        composeTestRule
            .onNodeWithText(Gender.MALE.displayName)
            .assertExists()
            .assertIsEnabled()

        composeTestRule
            .onNodeWithText(Gender.FEMALE.displayName)
            .assertExists()
            .assertIsEnabled()
    }

    @Test
    fun `gender field is disabled in view mode`() {
        // Given
        val testResident = Resident.default.copy(
            gender = Gender.FEMALE.displayName
        )
        every { mockViewModel.state } returns mockState
        mockState.value = ResidentWindowState(resident = testResident)

        // When
        composeTestRule.setContent {
            ResidentTab(
                residentId = UUID.randomUUID(),
                viewModel = mockViewModel,
                mode = WindowMode.VIEW
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(Gender.FEMALE.displayName)
            .assertExists()
            .assertIsNotEnabled()

        // Verify dropdown icon is not visible in view mode
        composeTestRule
            .onNode(hasContentDescription("Open gender selection"))
            .assertDoesNotExist()
    }
}
