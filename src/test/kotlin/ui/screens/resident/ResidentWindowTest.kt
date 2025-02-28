import models.Qualification
import models.Resident
import viewmodel.ResidentWindowViewModel
import java.time.LocalDate
import database.dao.ResidentDao
import database.dao.QualificationDao
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.doThrow
import ui.screens.resident.WindowMode
import ui.screens.resident.isTabDisabled

@ExperimentalCoroutinesApi
class ResidentWindowTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val residentDao: ResidentDao = mock()
    private val qualificationDao: QualificationDao = mock()
    private lateinit var viewModel: ResidentWindowViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ResidentWindowViewModel(qualificationDao, residentDao, testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test save functionality updates database and state`() = testScope.runTest {
        // Arrange
        val resident = Resident.default.copy(
            id = UUID.randomUUID(),
            firstName = "Test",
            lastName = "User"
        )

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.UpdateResident(resident))

        // Wait for coroutines to complete
        advanceUntilIdle()

        // Assert
        verify(residentDao).updateResident(resident)
        assertTrue(viewModel.state.value.saveSuccess)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `test navigation buttons visibility`() = testScope.runTest {
        // Arrange
        val resident = Resident.default.copy(
            id = UUID.randomUUID(),
            firstName = "Test",
            lastName = "User"
        )
        whenever(residentDao.getResidentById(resident.id)).thenReturn(resident)

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(resident.id))
        advanceUntilIdle()

        // Assert
        assertFalse(isTabDisabled(1, WindowMode.VIEW, viewModel))
        assertFalse(isTabDisabled(2, WindowMode.VIEW, viewModel))
    }

    @Test
    fun `test resident loading`() = testScope.runTest {
        // Arrange
        val residentId = UUID.randomUUID()
        val resident = Resident.default.copy(
            id = residentId,
            firstName = "Test",
            lastName = "User"
        )
        whenever(residentDao.getResidentById(residentId)).thenReturn(resident)

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))

        // Wait for coroutines to complete
        advanceUntilIdle()

        // Assert
        verify(residentDao).getResidentById(residentId)
        assertEquals(resident, viewModel.state.value.resident)
    }

    @Test
    fun `test mode toggle transitions between VIEW and UPDATE`() = testScope.runTest {
        // Arrange - Load a valid resident first
        val residentId = UUID.randomUUID()
        val resident = Resident.default.copy(
            id = residentId,
            firstName = "Test",
            lastName = "User"
        )
        whenever(residentDao.getResidentById(residentId)).thenReturn(resident)
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))
        advanceUntilIdle()

        // Act & Assert - Toggle mode
        assertEquals(WindowMode.VIEW, viewModel.state.value.mode)
        viewModel.processIntent(ResidentWindowViewModel.Intent.ToggleMode)
        advanceUntilIdle()
        assertEquals(WindowMode.UPDATE, viewModel.state.value.mode)
        viewModel.processIntent(ResidentWindowViewModel.Intent.ToggleMode)
        advanceUntilIdle()
        assertEquals(WindowMode.VIEW, viewModel.state.value.mode)
    }

    @Test
    fun `test create resident functionality`() = testScope.runTest {
        // Arrange
        val newResident = Resident.default.copy(
            id = UUID.randomUUID(),
            firstName = "New",
            lastName = "User"
        )

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.CreateResident(newResident))
        advanceUntilIdle()

        // Assert
        verify(residentDao).createResident(newResident)
        assertTrue(viewModel.state.value.saveSuccess)
        assertEquals(WindowMode.VIEW, viewModel.state.value.mode)
        assertEquals(newResident, viewModel.state.value.resident)
    }

    @Test
    fun `test error handling during save`() = testScope.runTest {
        // Arrange
        val residentId = UUID.randomUUID()
        val resident = Resident.default.copy(
            id = residentId,
            firstName = "Test",
            lastName = "User"
        )
        val errorMessage = "Database error"

        // Set up mocks
        whenever(residentDao.getResidentById(residentId)).thenReturn(resident)
        doThrow(RuntimeException(errorMessage)).`when`(residentDao).updateResident(resident)

        // Load the resident
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))
        advanceUntilIdle()

        // Verify resident is loaded
        verify(residentDao).getResidentById(residentId)
        assertEquals(resident, viewModel.state.value.resident)
        println("[DEBUG_LOG] Resident loaded: ${viewModel.state.value.resident}")

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.UpdateResident(resident))
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.state.value.saveSuccess)
        assertEquals(errorMessage, viewModel.state.value.error)
    }

    @Test
    fun `test qualification error handling during update`() = testScope.runTest {
        // Arrange
        val qualification = Qualification(
            id = UUID.randomUUID(),
            residentId = UUID.randomUUID(),
            name = "Test Qualification",
            institution = "Test Institution",
            nqfLevel = 7,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            city = "Test City"
        )
        val errorMessage = "Database error"
        doThrow(RuntimeException(errorMessage)).`when`(qualificationDao).updateQualification(qualification)

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.UpdateQualification(qualification))
        advanceUntilIdle()

        // Assert
        verify(qualificationDao).updateQualification(qualification)
        assertFalse(viewModel.state.value.saveSuccess)
        assertEquals(errorMessage, viewModel.state.value.error)
    }

    @Test
    fun `test qualification error handling during creation`() = testScope.runTest {
        // Arrange
        val qualification = Qualification(
            id = UUID.randomUUID(),
            residentId = UUID.randomUUID(),
            name = "Test Qualification",
            institution = "Test Institution",
            nqfLevel = 7,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            city = "Test City"
        )
        val errorMessage = "Database error"
        doThrow(RuntimeException(errorMessage)).`when`(qualificationDao).createQualification(qualification)

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.CreateQualification(qualification))
        advanceUntilIdle()

        // Assert
        verify(qualificationDao).createQualification(qualification)
        assertFalse(viewModel.state.value.saveSuccess)
        assertEquals(errorMessage, viewModel.state.value.error)
    }

    @Test
    fun `test qualification error handling during loading`() = testScope.runTest {
        // Arrange
        val residentId = UUID.randomUUID()
        val errorMessage = "Database error"
        doThrow(RuntimeException(errorMessage)).`when`(qualificationDao).getQualificationsByResidentId(residentId)

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadQualifications(residentId))
        advanceUntilIdle()

        // Assert
        verify(qualificationDao).getQualificationsByResidentId(residentId)
        assertTrue(viewModel.state.value.qualifications.isEmpty())
        assertEquals(errorMessage, viewModel.state.value.error)
    }

    @Test
    fun `test mode switches to VIEW after successful save`() = testScope.runTest {
        // Arrange
        val residentId = UUID.randomUUID()
        val resident = Resident.default.copy(
            id = residentId,
            firstName = "Test",
            lastName = "User"
        )

        // Set up mocks and load resident
        whenever(residentDao.getResidentById(residentId)).thenReturn(resident)
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))
        advanceUntilIdle()

        // Verify resident is loaded
        verify(residentDao).getResidentById(residentId)
        assertEquals(resident, viewModel.state.value.resident)
        println("[DEBUG_LOG] Initial resident state: ${viewModel.state.value.resident}")
        assertEquals(WindowMode.VIEW, viewModel.state.value.mode)

        // Switch to UPDATE mode
        viewModel.processIntent(ResidentWindowViewModel.Intent.ToggleMode)
        advanceUntilIdle()
        println("[DEBUG_LOG] Mode after toggle: ${viewModel.state.value.mode}")
        assertEquals(WindowMode.UPDATE, viewModel.state.value.mode)

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.UpdateResident(resident))
        advanceUntilIdle()

        // Assert
        println("[DEBUG_LOG] Final state - mode: ${viewModel.state.value.mode}, resident: ${viewModel.state.value.resident}")
        assertTrue(viewModel.state.value.saveSuccess)
        assertEquals(WindowMode.VIEW, viewModel.state.value.mode)
    }
}
