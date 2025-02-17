package database.dao

import database.schema.Addresses
import database.schema.Residences
import models.Address
import models.Residence
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class ResidenceDaoTest {

    private val residenceDao = ResidenceDao()

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Residences, Addresses)
        }
    }

    @Test
    fun testCreateResidence() {
        val address = createTestAddress()
        val residence = Residence(UUID.randomUUID(), UUID.randomUUID(), address.id, LocalDate.parse("2023-01-01"))
        val createdResidence = residenceDao.createResidence(residence)
        assertNotNull(createdResidence.id)
    }

    @Test
    fun testGetResidenceById() {
        val address = createTestAddress()
        val residence = Residence(UUID.randomUUID(), UUID.randomUUID(), address.id, LocalDate.parse("2023-01-01"))
        val createdResidence = residenceDao.createResidence(residence)
        val fetchedResidence = residenceDao.getResidenceById(createdResidence.id)
        assertNotNull(fetchedResidence)
        assertEquals(createdResidence.id, fetchedResidence?.id)
    }

    @Test
    fun testUpdateResidence() {
        val address = createTestAddress()
        val residence = Residence(UUID.randomUUID(), UUID.randomUUID(), address.id, LocalDate.parse("2023-01-01"))
        val createdResidence = residenceDao.createResidence(residence)
        val updatedResidence = createdResidence.copy(occupationDate = LocalDate.parse("2023-02-01"))
        val updateResult = residenceDao.updateResidence(updatedResidence)
        assertTrue(updateResult)
        val fetchedResidence = residenceDao.getResidenceById(createdResidence.id)
        assertEquals(LocalDate.parse("2023-02-01"), fetchedResidence?.occupationDate)
    }

    @Test
    fun testDeleteResidence() {
        val address = createTestAddress()
        val residence = Residence(UUID.randomUUID(), UUID.randomUUID(), address.id, LocalDate.parse("2023-01-01"))
        val createdResidence = residenceDao.createResidence(residence)
        val deleteResult = residenceDao.deleteResidence(createdResidence.id)
        assertTrue(deleteResult)
        val fetchedResidence = residenceDao.getResidenceById(createdResidence.id)
        assertNull(fetchedResidence)
    }

    private fun createTestAddress(): Address {
        return transaction {
            val addressId = Addresses.insertAndGetId {
                it[line] = "Test Line"
                it[houseNumber] = "123"
                it[suburb] = "Test Suburb"
                it[town] = "Test Town"
                it[postalCode] = "12345"
                it[geoCoordinates] = "0,0"
                it[landmark] = "Test Landmark"
            }
            Address(
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
}