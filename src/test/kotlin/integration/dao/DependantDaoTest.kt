package integration.dao

import database.dao.DependantDaoImpl
import database.schema.Dependants
import database.schema.Residents
import models.Dependant
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Integration tests for DependantDao implementation.
 * Tests CRUD operations, search functionality, and resident-dependant relationships.
 *
 * Uses an in-memory SQLite database that is recreated for each test.
 * Special focus on testing the relationship between residents and their dependants.
 */
class DependantDaoTest {
    private val dependantDao = DependantDaoImpl()

    @BeforeEach
    fun setup() {
        println("[DEBUG_LOG] Setting up test database")
        // Use shared cache to maintain connection across transactions
        val db = Database.connect("jdbc:sqlite:file:test?mode=memory&cache=shared", driver = "org.sqlite.JDBC")
        println("[DEBUG_LOG] Database connection established: ${db.url}")

        transaction {
            println("[DEBUG_LOG] Creating database schema")
            SchemaUtils.drop(Dependants)
            SchemaUtils.drop(Residents)
            println("[DEBUG_LOG] Dropped existing tables")

            SchemaUtils.create(Residents)
            println("[DEBUG_LOG] Created Residents table")
            SchemaUtils.create(Dependants)
            println("[DEBUG_LOG] Created Dependants table")
        }
        println("[DEBUG_LOG] Database setup completed")
    }

    @Test
    fun testCreateDependant() {
        transaction {
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
    }

    @Test
    fun testGetDependantById() {
        transaction {
            val dependant = createTestDependant()
            val createdDependant = dependantDao.createDependant(dependant)

            val fetchedDependant = dependantDao.getDependantById(createdDependant.id)
            assertNotNull(fetchedDependant)
            assertEquals(createdDependant.name, fetchedDependant?.name)
            assertEquals(createdDependant.surname, fetchedDependant?.surname)
            assertEquals(createdDependant.gender, fetchedDependant?.gender)
        }
    }

    @Test
    fun testGetAllDependants() {
        transaction {
            val dependants = listOf(
                createTestDependant(),
                createTestDependant(),
                createTestDependant()
            )
            dependants.forEach { dependantDao.createDependant(it) }

            val fetchedDependants = dependantDao.getAllDependants()
            assertEquals(dependants.size, fetchedDependants.size)
        }
    }

    @Test
    fun testGetDependantsByResidentId() {
        transaction {
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
    }

    @Test
    fun testUpdateDependant() {
        transaction {
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
    }

    @Test
    fun testDeleteDependant() {
        transaction {
            val dependant = createTestDependant()
            val createdDependant = dependantDao.createDependant(dependant)

            val deleteResult = dependantDao.deleteDependant(createdDependant.id)
            assertTrue(deleteResult)

            val fetchedDependant = dependantDao.getDependantById(createdDependant.id)
            assertNull(fetchedDependant)
        }
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
