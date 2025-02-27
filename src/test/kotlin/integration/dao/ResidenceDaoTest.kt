package database.dao

import database.schema.Addresses
import database.schema.Residences
import database.schema.Residents
import models.Address
import models.Residence
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File.createTempFile
import java.time.LocalDate
import java.util.*

class ResidenceDaoTest {

    private val residenceDao = ResidenceDaoImpl()
    private val dbFile = createTempFile("test", ".db")
    private val db = Database.connect("jdbc:sqlite:${dbFile.absolutePath}", driver = "org.sqlite.JDBC")

    @BeforeEach
    fun setup() {
        transaction(db) {
            SchemaUtils.drop(Residences, Addresses, Residents)
            SchemaUtils.create(Residents, Addresses, Residences)
            commit() // Ensure schema changes are committed
        }
    }

    @AfterEach
    fun cleanup() {
        dbFile.delete() // Clean up the test database file
    }

    @Test
    fun testCreateResidence() = transaction(db) {
        val address = createTestAddress()
        val residentId = createTestResident()
        val residence = Residence(UUID.randomUUID(), residentId, address.id, LocalDate.parse("2023-01-01"))
        val createdResidence = residenceDao.createResidence(residence)
        assertNotNull(createdResidence.id)
    }

    @Test
    fun testGetResidenceById() = transaction(db) {
        val address = createTestAddress()
        val residentId = createTestResident()
        val residence = Residence(UUID.randomUUID(), residentId, address.id, LocalDate.parse("2023-01-01"))
        val createdResidence = residenceDao.createResidence(residence)
        val fetchedResidence = residenceDao.getResidenceById(createdResidence.id)
        assertNotNull(fetchedResidence)
        assertEquals(createdResidence.id, fetchedResidence?.id)
    }

    @Test
    fun testUpdateResidence() = transaction(db) {
        val address = createTestAddress()
        val residentId = createTestResident()
        val residence = Residence(UUID.randomUUID(), residentId, address.id, LocalDate.parse("2023-01-01"))
        val createdResidence = residenceDao.createResidence(residence)
        val updatedResidence = createdResidence.copy(occupationDate = LocalDate.parse("2023-02-01"))
        val updateResult = residenceDao.updateResidence(updatedResidence)
        assertTrue(updateResult)
        val fetchedResidence = residenceDao.getResidenceById(createdResidence.id)
        assertEquals(LocalDate.parse("2023-02-01"), fetchedResidence?.occupationDate)
    }

    @Test
    fun testDeleteResidence() = transaction(db) {
        val address = createTestAddress()
        val residentId = createTestResident()
        val residence = Residence(UUID.randomUUID(), residentId, address.id, LocalDate.parse("2023-01-01"))
        val createdResidence = residenceDao.createResidence(residence)
        val deleteResult = residenceDao.deleteResidence(createdResidence.id)
        assertTrue(deleteResult)
        val fetchedResidence = residenceDao.getResidenceById(createdResidence.id)
        assertNull(fetchedResidence)
    }

    private fun createTestResident(): UUID {
        return Residents.insertAndGetId {
            it[firstName] = "Test"
            it[lastName] = "Resident"
            it[dob] = LocalDate.of(1990, 1, 1)
            it[gender] = "Male"
            it[idNumber] = UUID.randomUUID().toString() // Ensure unique ID number
            it[phoneNumber] = "1234567890"
            it[email] = "test@example.com"
        }.value
    }

    private fun createTestAddress(): Address {
        val addressId = Addresses.insertAndGetId {
            it[line] = "Test Line"
            it[houseNumber] = "123"
            it[suburb] = "Test Suburb"
            it[town] = "Test Town"
            it[postalCode] = "12345"
            it[geoCoordinates] = "0,0"
            it[landmark] = "Test Landmark"
        }
        return Address(
            id = addressId.value,
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
