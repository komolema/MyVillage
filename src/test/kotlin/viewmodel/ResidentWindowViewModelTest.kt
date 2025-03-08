package viewmodel

import database.dao.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import models.Address
import models.Residence
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import io.mockk.mockk
import io.mockk.coEvery
import java.time.LocalDate
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ResidentWindowViewModelTest {
    private lateinit var viewModel: ResidentWindowViewModel
    private lateinit var residenceDao: ResidenceDao
    private lateinit var addressDao: AddressDao
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        residenceDao = mockk()
        addressDao = mockk()

        // Initialize other required DAOs with empty mocks
        val residentDao = mockk<ResidentDao>()
        val qualificationDao = mockk<QualificationDao>()
        val dependantDao = mockk<DependantDao>()
        val employmentDao = mockk<EmploymentDao>()

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
    fun `test loadResidence when residence exists`() = runTest {
        // Arrange
        val residentId = UUID.randomUUID()
        val residence = Residence(
            id = UUID.randomUUID(),
            residentId = residentId,
            addressId = UUID.randomUUID(),
            occupationDate = LocalDate.now()
        )
        val address = Address(
            id = residence.addressId,
            line = "123 Test St",
            houseNumber = "123",
            suburb = "Test Suburb",
            town = "Test Town",
            postalCode = "12345",
            geoCoordinates = null,
            landmark = null
        )

        coEvery { residenceDao.getResidenceByResidentId(residentId) } returns residence
        coEvery { addressDao.getById(residence.addressId) } returns address

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResidence(residentId))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertEquals(residence, state.residence)
        assertEquals(address, state.address)
        assertNull(state.error)
    }

    @Test
    fun `test loadResidence when residence does not exist`() = runTest {
        // Arrange
        val residentId = UUID.randomUUID()
        coEvery { residenceDao.getResidenceByResidentId(residentId) } returns null

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.LoadResidence(residentId))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertNull(state.residence)
        assertNull(state.address)
        assertNull(state.error)
    }

    @Test
    fun `test deleteResidence success`() = runTest {
        // Arrange
        val residenceId = UUID.randomUUID()
        val addressId = UUID.randomUUID()

        coEvery { residenceDao.deleteResidence(residenceId) } returns true
        coEvery { addressDao.delete(addressId) } returns Unit

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.DeleteResidence(residenceId, addressId))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertNull(state.residence)
        assertNull(state.address)
        assertTrue(state.saveSuccess)
        assertNull(state.error)
    }

    @Test
    fun `test deleteResidence when residence deletion fails`() = runTest {
        // Arrange
        val residenceId = UUID.randomUUID()
        val addressId = UUID.randomUUID()

        coEvery { residenceDao.deleteResidence(residenceId) } returns false

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.DeleteResidence(residenceId, addressId))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertNotNull(state.error)
        assertFalse(state.saveSuccess)
    }

    @Test
    fun `test deleteResidence when address deletion throws exception`() = runTest {
        // Arrange
        val residenceId = UUID.randomUUID()
        val addressId = UUID.randomUUID()

        coEvery { residenceDao.deleteResidence(residenceId) } returns true
        coEvery { addressDao.delete(addressId) } throws Exception("Failed to delete address")

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.DeleteResidence(residenceId, addressId))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertNotNull(state.error)
        assertFalse(state.saveSuccess)
    }

    @Test
    fun `test createResidence success`() = runTest {
        // Arrange
        val residentId = UUID.randomUUID()
        val addressId = UUID.randomUUID()
        val residence = Residence(
            id = UUID.randomUUID(),
            residentId = residentId,
            addressId = addressId,
            occupationDate = LocalDate.now()
        )
        val address = Address(
            id = addressId,
            line = "123 Test St",
            houseNumber = "123",
            suburb = "Test Suburb",
            town = "Test Town",
            postalCode = "12345",
            geoCoordinates = null,
            landmark = null
        )

        coEvery { addressDao.create(address) } returns Unit
        coEvery { residenceDao.createResidence(residence) } returns residence

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.CreateResidence(residence, address))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertEquals(residence, state.residence)
        assertEquals(address, state.address)
        assertTrue(state.saveSuccess)
        assertNull(state.error)
    }

    @Test
    fun `test createResidence when creation fails`() = runTest {
        // Arrange
        val residentId = UUID.randomUUID()
        val addressId = UUID.randomUUID()
        val residence = Residence(
            id = UUID.randomUUID(),
            residentId = residentId,
            addressId = addressId,
            occupationDate = LocalDate.now()
        )
        val address = Address(
            id = addressId,
            line = "123 Test St",
            houseNumber = "123",
            suburb = "Test Suburb",
            town = "Test Town",
            postalCode = "12345",
            geoCoordinates = null,
            landmark = null
        )

        coEvery { addressDao.create(address) } throws Exception("Failed to create address")

        // Act
        viewModel.processIntent(ResidentWindowViewModel.Intent.CreateResidence(residence, address))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.state.value
        assertNotNull(state.error)
        assertFalse(state.saveSuccess)
        assertNull(state.residence)
        assertNull(state.address)
    }
}
