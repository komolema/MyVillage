package database.dao

import database.schema.Addresses
import database.schema.Residences
import models.Address
import models.Residence
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.util.*

class ResidenceDaoTest {
    private val residenceDao = ResidenceDaoImpl()
    private val addressDao = AddressDaoImpl()

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Residences, Addresses)
        }
    }

    private fun createTestAddress(): Address {
        return Address(
            id = UUID.randomUUID(),
            line = "123 Test Street",
            houseNumber = "456",
            suburb = "Test Suburb",
            town = "Test Town",
            postalCode = "12345",
            geoCoordinates = "-25.7461,28.1881",
            landmark = "Near Test Park"
        )
    }

    private fun createTestResidence(residentId: UUID = UUID.randomUUID(), addressId: UUID): Residence {
        return Residence(
            id = UUID.randomUUID(),
            residentId = residentId,
            addressId = addressId,
            occupationDate = LocalDate.now().minusMonths(1)
        )
    }

    private fun insertTestAddress(address: Address) {
        addressDao.create(address)
    }

    @Test
    fun testCreateAndGetResidence() = transaction {
        val address = createTestAddress()
        insertTestAddress(address)

        val residence = createTestResidence(addressId = address.id)
        val createdResidence = residenceDao.createResidence(residence)

        assertNotNull(createdResidence.id)
        assertEquals(residence.residentId, createdResidence.residentId)
        assertEquals(residence.addressId, createdResidence.addressId)
    }

    @Test
    fun testGetResidenceByResidentId() = transaction {
        val address = createTestAddress()
        insertTestAddress(address)

        val residentId = UUID.randomUUID()
        val residence = createTestResidence(residentId = residentId, addressId = address.id)
        residenceDao.createResidence(residence)

        val fetchedResidence = residenceDao.getResidenceByResidentId(residentId)
        assertNotNull(fetchedResidence)
        assertEquals(residence.residentId, fetchedResidence?.residentId)
        assertEquals(residence.addressId, fetchedResidence?.addressId)
    }

    @Test
    fun testGetAddressByResidentId() = transaction {
        val address = createTestAddress()
        insertTestAddress(address)

        val residentId = UUID.randomUUID()
        val residence = createTestResidence(residentId = residentId, addressId = address.id)
        residenceDao.createResidence(residence)

        val fetchedAddress = residenceDao.getAddressByResidentId(residentId)
        assertNotNull(fetchedAddress)
        assertEquals(address.line, fetchedAddress?.line)
        assertEquals(address.houseNumber, fetchedAddress?.houseNumber)
        assertEquals(address.suburb, fetchedAddress?.suburb)
    }

    @Test
    fun testGetAllResidences() = transaction {
        val address = createTestAddress()
        insertTestAddress(address)

        val residences = listOf(
            createTestResidence(addressId = address.id),
            createTestResidence(addressId = address.id),
            createTestResidence(addressId = address.id)
        )
        residences.forEach { residenceDao.createResidence(it) }

        val allResidences = residenceDao.getAllResidences()
        assertEquals(residences.size, allResidences.size)
    }

    @Test
    fun testUpdateResidence() = transaction {
        val address = createTestAddress()
        insertTestAddress(address)

        val residence = createTestResidence(addressId = address.id)
        val createdResidence = residenceDao.createResidence(residence)

        val newAddress = createTestAddress()
        insertTestAddress(newAddress)

        val updatedResidence = createdResidence.copy(
            addressId = newAddress.id,
            occupationDate = LocalDate.now()
        )

        val updateResult = residenceDao.updateResidence(updatedResidence)
        assertTrue(updateResult)

        val fetchedResidence = residenceDao.getResidenceById(createdResidence.id)
        assertEquals(newAddress.id, fetchedResidence?.addressId)
        assertEquals(updatedResidence.occupationDate, fetchedResidence?.occupationDate)
    }

    @Test
    fun testDeleteResidence() = transaction {
        val address = createTestAddress()
        insertTestAddress(address)

        val residence = createTestResidence(addressId = address.id)
        val createdResidence = residenceDao.createResidence(residence)

        val deleteResult = residenceDao.deleteResidence(createdResidence.id)
        assertTrue(deleteResult)

        val fetchedResidence = residenceDao.getResidenceById(createdResidence.id)
        assertNull(fetchedResidence)
    }

    @Test
    fun testGetNonExistentResidence() = transaction {
        val nonExistentId = UUID.randomUUID()
        val residence = residenceDao.getResidenceById(nonExistentId)
        assertNull(residence)
    }

    @Test
    fun testGetNonExistentAddress() = transaction {
        val nonExistentId = UUID.randomUUID()
        val address = residenceDao.getAddressByResidentId(nonExistentId)
        assertNull(address)
    }
}
