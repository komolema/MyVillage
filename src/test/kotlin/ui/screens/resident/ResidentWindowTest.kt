import models.Qualification
import models.Resident
import viewmodel.ResidentWindowViewModel
import java.time.LocalDate
import database.dao.ResidentDao
import database.dao.QualificationDao
import database.dao.DependantDao
import database.dao.ResidenceDao
import database.dao.AddressDao
import database.dao.EmploymentDao
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import io.mockk.mockk
import io.mockk.verify
import io.mockk.every
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.runs
import ui.screens.resident.WindowMode
import ui.screens.resident.isTabDisabled

@ExperimentalCoroutinesApi
class ResidentWindowTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val residentDao = mockk<ResidentDao>(relaxed = true)
    private val qualificationDao = mockk<QualificationDao>(relaxed = true)
    private val dependantDao = mockk<DependantDao>(relaxed = true)
    private val residenceDao = mockk<ResidenceDao>(relaxed = true)
    private val addressDao = mockk<AddressDao>(relaxed = true)
    private val employmentDao = mockk<EmploymentDao>(relaxed = true)
    private lateinit var viewModel: ResidentWindowViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Initialize default mock behavior
        every { residentDao.getResidentById(any()) } returns null
        every { residentDao.updateResident(any()) } just runs
        every { residentDao.createResident(any()) } just runs
        every { qualificationDao.getQualificationsByResidentId(any()) } returns emptyList()
        every { qualificationDao.updateQualification(any()) } returns true
        every { qualificationDao.createQualification(any()) } returns Qualification.default
        coEvery { dependantDao.getDependantsByResidentId(any()) } returns emptyList()
        every { residenceDao.getResidenceByResidentId(any()) } returns null
        every { addressDao.getById(any()) } returns null
        every { employmentDao.getEmploymentByResidentId(any()) } returns emptyList()
        viewModel = ResidentWindowViewModel(
            qualificationDao = qualificationDao,
            residentDao = residentDao,
            dependantDao = dependantDao,
            residenceDao = residenceDao,
            addressDao = addressDao,
            employmentDao = employmentDao,
            dispatcher = testDispatcher
        )
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
        every { residentDao.updateResident(resident) } just runs

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.UpdateResident(resident))

        // Wait for coroutines to complete
        advanceUntilIdle()

        // Assert
        verify { residentDao.updateResident(resident) }
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
        every { residentDao.getResidentById(resident.id) } returns resident

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(resident.id))
        advanceUntilIdle()

        // Assert
        verify { residentDao.getResidentById(resident.id) }
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
        every { residentDao.getResidentById(residentId) } returns resident

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))

        // Wait for coroutines to complete
        advanceUntilIdle()

        // Assert
        verify { residentDao.getResidentById(residentId) }
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
        every { residentDao.getResidentById(residentId) } returns resident
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))
        advanceUntilIdle()

        // Verify initial load
        verify { residentDao.getResidentById(residentId) }
        assertEquals(resident, viewModel.state.value.resident)

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
        every { residentDao.createResident(newResident) } just runs

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.CreateResident(newResident))
        advanceUntilIdle()

        // Assert
        verify { residentDao.createResident(newResident) }
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
        every { residentDao.getResidentById(residentId) } returns resident
        every { residentDao.updateResident(resident) } throws RuntimeException(errorMessage)

        // Load the resident
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResident(residentId))
        advanceUntilIdle()

        // Verify resident is loaded
        verify { residentDao.getResidentById(residentId) }
        assertEquals(resident, viewModel.state.value.resident)
        println("[DEBUG_LOG] Resident loaded: ${viewModel.state.value.resident}")

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.UpdateResident(resident))
        advanceUntilIdle()

        // Assert
        verify { residentDao.updateResident(resident) }
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
        every { qualificationDao.updateQualification(qualification) } throws RuntimeException(errorMessage)

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.UpdateQualification(qualification))
        advanceUntilIdle()

        // Assert
        verify { qualificationDao.updateQualification(qualification) }
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
        every { qualificationDao.createQualification(qualification) } throws RuntimeException(errorMessage)

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.CreateQualification(qualification))
        advanceUntilIdle()

        // Assert
        verify { qualificationDao.createQualification(qualification) }
        assertFalse(viewModel.state.value.saveSuccess)
        assertEquals(errorMessage, viewModel.state.value.error)
    }

    @Test
    fun `test qualification error handling during loading`() = testScope.runTest {
        // Arrange
        val residentId = UUID.randomUUID()
        val errorMessage = "Database error"
        every { qualificationDao.getQualificationsByResidentId(residentId) } throws RuntimeException(errorMessage)

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadQualifications(residentId))
        advanceUntilIdle()

        // Assert
        verify { qualificationDao.getQualificationsByResidentId(residentId) }
        assertTrue(viewModel.state.value.qualifications.isEmpty())
        assertEquals(errorMessage, viewModel.state.value.error)
    }

    @Test
    fun `test successful save in NEW mode`() = testScope.runTest {
        // Create a new ViewModel instance with NEW mode
        val newViewModel = ResidentWindowViewModel(
            qualificationDao = qualificationDao,
            residentDao = residentDao,
            dependantDao = dependantDao,
            residenceDao = residenceDao,
            addressDao = addressDao,
            employmentDao = employmentDao,
            initialMode = WindowMode.NEW,
            dispatcher = testDispatcher
        )

        // Arrange
        val resident = Resident.default.copy(
            firstName = "New",
            lastName = "User"
        )

        // Verify initial mode is NEW
        assertEquals(WindowMode.NEW, newViewModel.state.value.mode)
        every { residentDao.createResident(resident) } just runs

        // Update resident data
        newViewModel.processIntent(ResidentWindowViewModel.Intent.UpdateResident(resident))
        advanceUntilIdle()

        // Act - Save the new resident
        newViewModel.processIntent(ResidentWindowViewModel.Intent.CreateResident(resident))
        advanceUntilIdle()

        // Assert
        verify { residentDao.createResident(resident) }
        assertTrue(newViewModel.state.value.saveSuccess)
        assertEquals(WindowMode.VIEW, newViewModel.state.value.mode)
    }
}
