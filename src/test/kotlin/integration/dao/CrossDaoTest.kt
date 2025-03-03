package integration.dao

import database.dao.*
import database.schema.*
import models.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlinx.coroutines.test.runTest

/**
 * Integration tests for cross-DAO interactions and relationships.
 * Tests the integration between different DAOs and ensures proper relationship handling.
 *
 * Uses an in-memory SQLite database that is recreated for each test.
 * Key test scenarios include:
 * - Resident and dependant relationships
 * - Resident and address associations
 * - Cascading deletes and updates
 * - Data integrity across related entities
 */
class CrossDaoTest {
    private val residentDao = ResidentDaoImpl(ResidenceDaoImpl(), DependantDaoImpl())
    private val dependantDao = DependantDaoImpl()
    private val addressDao = AddressDaoImpl()
    private val resourceDao = ResourceDaoImpl()

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Residents, Dependants, Addresses, Resources)
        }
    }

    @Test
    fun testResidentWithDependants() = runTest {
        // Create a resident
        val resident = createTestResident()
        residentDao.createResident(resident)
        val createdResident = residentDao.getResidentById(resident.id)
        assertNotNull(createdResident)

        // Create dependants for the resident
        createdResident?.let { resident ->
            val dependant1 = createTestDependant(resident.id)
            val dependant2 = createTestDependant(resident.id)
            dependantDao.createDependant(dependant1)
            dependantDao.createDependant(dependant2)

            // Verify dependants are correctly associated
            val dependants = dependantDao.getDependantsByResidentId(resident.id)
        assertEquals(2, dependants.size)
            assertTrue(dependants.any { it.id == dependant1.id })
            assertTrue(dependants.any { it.id == dependant2.id })
        }

        // Verify expanded resident includes dependants
        val expandedResident = residentDao.getResidentExpandedById(resident.id)
        assertNotNull(expandedResident)
        assertEquals(2, expandedResident?.dependants?.size)
    }

    @Test
    fun testResidentWithAddress() = runTest {
        // Create an address
        val address = createTestAddress()
        addressDao.create(address)

        // Create a resident with the address
        val resident = createTestResident()
        residentDao.createResident(resident)

        // Verify the relationship
        val expandedResident = residentDao.getResidentExpandedById(resident.id)
        assertNotNull(expandedResident)
        assertNotNull(expandedResident?.address)
    }

    @Test
    fun testCascadingDelete() = runTest {
        // Create a resident with dependants
        val resident = createTestResident()
        residentDao.createResident(resident)

        val dependant1 = createTestDependant(resident.id)
        val dependant2 = createTestDependant(resident.id)
        dependantDao.createDependant(dependant1)
        dependantDao.createDependant(dependant2)

        // Delete the resident
        residentDao.delete(resident.id)

        // Verify dependants are also deleted
        val remainingDependants = dependantDao.getDependantsByResidentId(resident.id)
        assertTrue(remainingDependants.isEmpty())
    }

    private fun createTestResident(): Resident {
        return Resident(
            id = UUID.randomUUID(),
            idNumber = "TEST123",
            firstName = "Test",
            lastName = "Resident",
            dob = java.time.LocalDate.now(),
            gender = "M",
            phoneNumber = "1234567890",
            email = "test@example.com"
        )
    }

    private fun createTestDependant(residentId: UUID): Dependant {
        return Dependant(
            id = UUID.randomUUID(),
            residentId = residentId,
            idNumber = "DEP123",
            name = "Test",
            surname = "Dependant",
            gender = "F"
        )
    }

    private fun createTestAddress(): Address {
        return Address(
            id = UUID.randomUUID(),
            line = "Test Street",
            houseNumber = "123",
            suburb = "Test Suburb",
            town = "Test Town",
            postalCode = "12345",
            geoCoordinates = "0,0",
            landmark = "Test Landmark"
        )
    }
}
