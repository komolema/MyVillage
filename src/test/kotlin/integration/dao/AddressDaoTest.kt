package integration.dao

import database.dao.AddressDaoImpl
import database.schema.Addresses
import models.Address
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Integration tests for AddressDao implementation.
 * Tests CRUD operations, search functionality, and pagination.
 *
 * Uses an in-memory SQLite database that is recreated for each test.
 */
class AddressDaoTest {
    private val addressDao = AddressDaoImpl()

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Addresses)
        }
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
