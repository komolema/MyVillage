package integration.dao

import database.TestTransactionProvider
import database.TransactionProvider
import database.dao.domain.AddressDaoImpl
import database.schema.domain.Addresses
import models.domain.Address
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.io.createTempFile

/**
 * Integration tests for AddressDao implementation.
 * Tests CRUD operations, search functionality, and pagination.
 *
 * Uses an in-memory SQLite database that is recreated for each test.
 */
class AddressDaoTest {
    private lateinit var testTransactionProvider: TestTransactionProvider
    private lateinit var addressDao: AddressDaoImpl

    companion object {
        private lateinit var db: Database
        private val dbFile = createTempFile("test_db", ".db")

        @JvmStatic
        @BeforeAll
        fun setupClass() {
            println("[DEBUG_LOG] Setting up test class")
            println("[DEBUG_LOG] Using test database at: ${dbFile.absolutePath}")

            // Create a single database connection for all tests
            db = Database.connect(
                url = "jdbc:sqlite:${dbFile.absolutePath}",
                driver = "org.sqlite.JDBC"
            )
            println("[DEBUG_LOG] Database connected")

            // Initialize schema once for all tests
            transaction(db) {
                println("[DEBUG_LOG] Creating schema")
                SchemaUtils.create(Addresses)
                commit()

                // Verify schema creation
                val tableExists = exec("SELECT name FROM sqlite_master WHERE type='table' AND name='Addresses'") { 
                    it.next()
                }
                println("[DEBUG_LOG] Schema creation verified: $tableExists")
            }
        }

        @JvmStatic
        @AfterAll
        fun tearDownClass() {
            println("[DEBUG_LOG] Cleaning up test class")
            dbFile.delete()
            println("[DEBUG_LOG] Test database deleted")
        }
    }

    @BeforeEach
    fun setup() {
        println("[DEBUG_LOG] Setting up test")
        transaction(db) {
            // Clear all data before each test
            exec("DELETE FROM Addresses")
            commit()
            println("[DEBUG_LOG] Tables cleared")

            // Verify table is empty
            val count = Addresses.selectAll().count()
            println("[DEBUG_LOG] Table row count: $count")
        }

        // Initialize the DAO with the TestTransactionProvider
        testTransactionProvider = TestTransactionProvider(db)
        addressDao = AddressDaoImpl(testTransactionProvider)
    }

    @Test
    fun testCreateAddress() {
        val address = createTestAddress()
        addressDao.create(address)
        val fetchedAddress = addressDao.getById(address.id)
        assertNotNull(fetchedAddress)
        assertEquals(address.line, fetchedAddress?.line)
        assertEquals(address.houseNumber, fetchedAddress?.houseNumber)
    }

    @Test
    fun testGetAddressById() {
        val address = createTestAddress()
        addressDao.create(address)
        val fetchedAddress = addressDao.getById(address.id)
        assertNotNull(fetchedAddress)
        assertEquals(address.id, fetchedAddress?.id)
        assertEquals(address.line, fetchedAddress?.line)
        assertEquals(address.suburb, fetchedAddress?.suburb)
    }

    @Test
    fun testUpdateAddress() {
        val address = createTestAddress()
        addressDao.create(address)

        val updatedAddress = address.copy(
            line = "Updated Line",
            suburb = "Updated Suburb"
        )
        addressDao.update(updatedAddress)

        val fetchedAddress = addressDao.getById(address.id)
        assertNotNull(fetchedAddress)
        assertEquals("Updated Line", fetchedAddress?.line)
        assertEquals("Updated Suburb", fetchedAddress?.suburb)
    }

    @Test
    fun testDeleteAddress() {
        val address = createTestAddress()
        addressDao.create(address)

        addressDao.delete(address.id)

        val fetchedAddress = addressDao.getById(address.id)
        assertNull(fetchedAddress)
    }

    @Test
    fun testSearchAddress() {
        val address1 = createTestAddress()
        val address2 = createTestAddress().copy(
            id = UUID.randomUUID(),
            line = "Different Street",
            suburb = "Different Suburb"
        )

        addressDao.create(address1)
        addressDao.create(address2)

        val searchResults = addressDao.search("Test", 0, 10)
        assertTrue(searchResults.any { it.id == address1.id })
        assertFalse(searchResults.any { it.id == address2.id })
    }

    @Test
    fun testGetByIdNonExistent() {
        val nonExistentId = UUID.randomUUID()
        val fetchedAddress = addressDao.getById(nonExistentId)
        assertNull(fetchedAddress, "Fetching non-existent address should return null")
    }

    @Test
    fun testGetByIdWithNullFields() {
        val addressWithNulls = Address(
            id = UUID.randomUUID(),
            line = "Test Line",
            houseNumber = "123",
            suburb = "Test Suburb",
            town = "Test Town",
            postalCode = "12345",
            geoCoordinates = null,
            landmark = null
        )

        addressDao.create(addressWithNulls)
        val fetchedAddress = addressDao.getById(addressWithNulls.id)

        assertNotNull(fetchedAddress, "Address should be retrieved")
        assertEquals(addressWithNulls.id, fetchedAddress?.id)
        assertNull(fetchedAddress?.geoCoordinates, "GeoCoordinates should be null")
        assertNull(fetchedAddress?.landmark, "Landmark should be null")
    }

    @Test
    fun testGetByIdWithMultipleAddresses() {
        // Create multiple addresses
        val address1 = createTestAddress()
        val address2 = createTestAddress().copy(id = UUID.randomUUID(), line = "Another Line")
        val address3 = createTestAddress().copy(id = UUID.randomUUID(), line = "Yet Another Line")

        addressDao.create(address1)
        addressDao.create(address2)
        addressDao.create(address3)

        // Verify we can get the correct address
        val fetchedAddress = addressDao.getById(address2.id)

        assertNotNull(fetchedAddress, "Address should be retrieved")
        assertEquals(address2.id, fetchedAddress?.id)
        assertEquals("Another Line", fetchedAddress?.line)
    }

    @Test
    fun testPagination() {
        val addresses = (1..5).map {
            createTestAddress().copy(
                id = UUID.randomUUID(),
                line = "Line $it"
            )
        }
        addresses.forEach { addressDao.create(it) }

        val page1 = addressDao.getAll(0, 2)
        val page2 = addressDao.getAll(1, 2)
        val page3 = addressDao.getAll(2, 2)

        assertEquals(2, page1.size)
        assertEquals(2, page2.size)
        assertEquals(1, page3.size)
    }

    private fun createTestAddress(): Address {
        return Address(
            id = UUID.randomUUID(),
            line = "Test Line",
            houseNumber = "123",
            suburb = "Test Suburb",
            town = "Test Town",
            postalCode = "12345",
            geoCoordinates = "0,0",
            landmark = "Test Landmark"
        )
    }
}
