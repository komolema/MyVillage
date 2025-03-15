package integration.dao

import database.dao.domain.DependantDaoImpl
import database.dao.domain.ResidenceDaoImpl
import database.dao.domain.ResidentDaoImpl
import database.schema.domain.Residents
import models.domain.Resident
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File.createTempFile
import java.time.LocalDate
import java.util.*

class ResidentDaoTest {
    private val residentDao = ResidentDaoImpl(ResidenceDaoImpl(), DependantDaoImpl())
    private val dbFile = createTempFile("test", ".db")
    private val db = Database.connect("jdbc:sqlite:${dbFile.absolutePath}", driver = "org.sqlite.JDBC")

    @BeforeEach
    fun setup() {
        transaction(db) {
            SchemaUtils.drop(Residents)
            SchemaUtils.create(Residents)
            commit()
        }
    }

    @AfterEach
    fun cleanup() {
        dbFile.delete()
    }

    @Test
    fun testCreateAndGetResident() = transaction(db) {
        val resident = createTestResident()
        residentDao.createResident(resident)

        val fetchedResident = residentDao.getResidentById(resident.id)
        assertNotNull(fetchedResident)
        assertEquals(resident.firstName, fetchedResident?.firstName)
        assertEquals(resident.lastName, fetchedResident?.lastName)
        assertEquals(resident.idNumber, fetchedResident?.idNumber)
    }

    @Test
    fun testUpdateResident() = transaction(db) {
        val resident = createTestResident()
        residentDao.createResident(resident)

        val updatedResident = resident.copy(
            firstName = "UpdatedFirstName",
            lastName = "UpdatedLastName",
            gender = "Female"
        )

        residentDao.updateResident(updatedResident)

        val fetchedResident = residentDao.getResidentById(resident.id)
        assertNotNull(fetchedResident)
        assertEquals("UpdatedFirstName", fetchedResident?.firstName)
        assertEquals("UpdatedLastName", fetchedResident?.lastName)
        assertEquals("Female", fetchedResident?.gender)
        // Verify unchanged fields remain the same
        assertEquals(resident.email, fetchedResident?.email)
        assertEquals(resident.phoneNumber, fetchedResident?.phoneNumber)
    }

    @Test
    fun testDeleteResident() = transaction(db) {
        val resident = createTestResident()
        residentDao.createResident(resident)

        residentDao.delete(resident.id)

        val fetchedResident = residentDao.getResidentById(resident.id)
        assertNull(fetchedResident)
    }

    private fun createTestResident(): Resident {
        return Resident(
            id = UUID.randomUUID(),
            idNumber = "TEST${UUID.randomUUID()}",
            firstName = "TestFirst",
            lastName = "TestLast",
            dob = LocalDate.of(1990, 1, 1),
            gender = "Male",
            phoneNumber = "1234567890",
            email = "test@example.com"
        )
    }
}
