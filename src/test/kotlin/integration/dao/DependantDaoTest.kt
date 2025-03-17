package integration.dao

import database.TestTransactionProvider
import database.dao.domain.DependantDaoImpl
import database.schema.domain.Dependants
import database.schema.domain.Residents
import models.domain.Dependant
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
 * Integration tests for DependantDao implementation.
 * Tests CRUD operations, search functionality, and resident-dependant relationships.
 *
 * Uses a SQLite database that is recreated for each test.
 * Special focus on testing the relationship between residents and their dependants.
 */
class DependantDaoTest {
    private lateinit var testTransactionProvider: TestTransactionProvider
    private lateinit var dependantDao: DependantDaoImpl

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
                SchemaUtils.create(Residents)
                SchemaUtils.create(Dependants)
                commit()

                // Verify schema creation
                val tableExists = exec("SELECT name FROM sqlite_master WHERE type='table' AND name='Dependants'") { 
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
            exec("DELETE FROM Dependants")
            commit()
            println("[DEBUG_LOG] Tables cleared")
        }

        // Initialize the DAO with the TestTransactionProvider
        testTransactionProvider = TestTransactionProvider(db)
        dependantDao = DependantDaoImpl(testTransactionProvider)
    }

    @Test
    fun testCreateDependant() {
        val dependant = createTestDependant()
        val createdDependant = dependantDao.createDependant(dependant)

        assertNotNull(createdDependant.id)
        assertEquals(dependant.name, createdDependant.name)
        assertEquals(dependant.surname, createdDependant.surname)
        assertEquals(dependant.gender, createdDependant.gender)

        val fetchedDependant = dependantDao.getDependantById(createdDependant.id)
        assertNotNull(fetchedDependant)
        assertEquals(createdDependant.name, fetchedDependant?.name)
        assertEquals(createdDependant.surname, fetchedDependant?.surname)
    }

    @Test
    fun testGetDependantById() {
        val dependant = createTestDependant()
        val createdDependant = dependantDao.createDependant(dependant)

        val fetchedDependant = dependantDao.getDependantById(createdDependant.id)
        assertNotNull(fetchedDependant)
        assertEquals(createdDependant.name, fetchedDependant?.name)
        assertEquals(createdDependant.surname, fetchedDependant?.surname)
        assertEquals(createdDependant.gender, fetchedDependant?.gender)
    }

    @Test
    fun testGetAllDependants() {
        val dependants = listOf(
            createTestDependant(),
            createTestDependant(),
            createTestDependant()
        )
        dependants.forEach { dependantDao.createDependant(it) }

        val fetchedDependants = dependantDao.getAllDependants()
        assertEquals(dependants.size, fetchedDependants.size)
    }

    @Test
    fun testGetDependantsByResidentId() {
        val residentId = UUID.randomUUID()
        val dependant1 = createTestDependant(residentId)
        val dependant2 = createTestDependant(residentId)
        val dependant3 = createTestDependant(UUID.randomUUID()) // Different resident

        dependantDao.createDependant(dependant1)
        dependantDao.createDependant(dependant2)
        dependantDao.createDependant(dependant3)

        val dependants = dependantDao.getDependantsByResidentId(residentId)
        assertEquals(2, dependants.size)
        assertTrue(dependants.any { it.id == dependant1.id })
        assertTrue(dependants.any { it.id == dependant2.id })
        assertFalse(dependants.any { it.id == dependant3.id })
    }

    @Test
    fun testUpdateDependant() {
        val dependant = createTestDependant()
        val createdDependant = dependantDao.createDependant(dependant)

        val updatedDependant = createdDependant.copy(
            name = "Updated Name",
            surname = "Updated Surname"
        )
        val updateResult = dependantDao.updateDependant(updatedDependant)
        assertTrue(updateResult)

        val fetchedDependant = dependantDao.getDependantById(createdDependant.id)
        assertNotNull(fetchedDependant)
        assertEquals("Updated Name", fetchedDependant?.name)
        assertEquals("Updated Surname", fetchedDependant?.surname)
    }

    @Test
    fun testDeleteDependant() {
        val dependant = createTestDependant()
        val createdDependant = dependantDao.createDependant(dependant)

        val deleteResult = dependantDao.deleteDependant(createdDependant.id)
        assertTrue(deleteResult)

        val fetchedDependant = dependantDao.getDependantById(createdDependant.id)
        assertNull(fetchedDependant)
    }

    @Test
    fun testInvalidGenderValue() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Dependant(
                id = UUID.randomUUID(),
                residentId = UUID.randomUUID(),
                idNumber = "TEST123",
                name = "Test Name",
                surname = "Test Surname",
                gender = "Invalid"
            )
        }
        assertTrue(exception.message?.contains("Invalid gender value") == true)
    }

    @Test
    fun testValidGenderValues() {
        transaction {
            Dependant.VALID_GENDERS.forEach { gender ->
                val dependant = createTestDependant(gender = gender)
                val createdDependant = dependantDao.createDependant(dependant)
                assertEquals(gender, createdDependant.gender)
            }
        }
    }

    private fun createTestDependant(
        residentId: UUID = UUID.randomUUID(),
        gender: String = Dependant.VALID_GENDERS.first()
    ): Dependant {
        return Dependant(
            id = UUID.randomUUID(),
            residentId = residentId,
            idNumber = "TEST123",
            name = "Test Name",
            surname = "Test Surname",
            gender = gender
        )
    }
}
