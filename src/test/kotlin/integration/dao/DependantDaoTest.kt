package integration.dao

import database.dao.DependantDaoImpl
import database.schema.Dependants
import models.Dependant
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlinx.coroutines.test.runTest

/**
 * Integration tests for DependantDao implementation.
 * Tests CRUD operations, search functionality, pagination, and resident-dependant relationships.
 *
 * Uses an in-memory SQLite database that is recreated for each test.
 * Special focus on testing the relationship between residents and their dependants,
 * including the getDependentsByResidentId functionality.
 */
class DependantDaoTest {
    private val dependantDao = DependantDaoImpl()

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Dependants)
        }
    }

    @Test
    fun testCreateDependant() = runTest {
        val dependant = createTestDependant()
        val createdDependant = dependantDao.createDependant(dependant)
        val fetchedDependants = dependantDao.getDependantsByResidentId(createdDependant.residentId)
        assertEquals(1, fetchedDependants.size)
        val fetchedDependant = fetchedDependants.first()
        assertEquals(dependant.name, fetchedDependant.name)
        assertEquals(dependant.surname, fetchedDependant.surname)
    }

    @Test
    fun testGetDependantByResidentId() = runTest {
        val dependant = createTestDependant()
        val createdDependant = dependantDao.createDependant(dependant)
        val fetchedDependants = dependantDao.getDependantsByResidentId(createdDependant.residentId)
        assertEquals(1, fetchedDependants.size)
        val fetchedDependant = fetchedDependants.first()
        assertEquals(dependant.id, fetchedDependant.id)
        assertEquals(dependant.name, fetchedDependant.name)
        assertEquals(dependant.surname, fetchedDependant.surname)
    }

    @Test
    fun testUpdateDependant() = runTest {
        val dependant = createTestDependant()
        val createdDependant = dependantDao.createDependant(dependant)

        val updatedDependant = createdDependant.copy(
            name = "Updated Name",
            surname = "Updated Surname"
        )
        val savedDependant = dependantDao.updateDependant(updatedDependant)

        val fetchedDependants = dependantDao.getDependantsByResidentId(savedDependant.residentId)
        assertEquals(1, fetchedDependants.size)
        val fetchedDependant = fetchedDependants.first()
        assertEquals("Updated Name", fetchedDependant.name)
        assertEquals("Updated Surname", fetchedDependant.surname)
    }

    @Test
    fun testDeleteDependant() = runTest {
        val dependant = createTestDependant()
        val createdDependant = dependantDao.createDependant(dependant)

        dependantDao.deleteDependant(createdDependant.id)

        val dependants = dependantDao.getDependantsByResidentId(createdDependant.residentId)
        assertFalse(dependants.any { it.id == createdDependant.id })
    }

    @Test
    fun testGetDependentsByResidentId() = runTest {
        val residentId = UUID.randomUUID()
        val dependant1 = createTestDependant(residentId)
        val dependant2 = createTestDependant(residentId)
        val dependant3 = createTestDependant(UUID.randomUUID()) // Different resident

        val created1 = dependantDao.createDependant(dependant1)
        val created2 = dependantDao.createDependant(dependant2)
        val created3 = dependantDao.createDependant(dependant3)

        val dependants = dependantDao.getDependantsByResidentId(residentId)
        assertEquals(2, dependants.size)
        assertTrue(dependants.any { it.id == dependant1.id })
        assertTrue(dependants.any { it.id == dependant2.id })
        assertFalse(dependants.any { it.id == dependant3.id })
    }

    @Test
    fun testGetDependantsByResidentIdWithMultipleResidents() = runTest {
        val residentId1 = UUID.randomUUID()
        val residentId2 = UUID.randomUUID()

        val dependant1 = createTestDependant(residentId1)
        val dependant2 = createTestDependant(residentId2)

        dependantDao.createDependant(dependant1)
        dependantDao.createDependant(dependant2)

        val resident1Dependants = dependantDao.getDependantsByResidentId(residentId1)
        val resident2Dependants = dependantDao.getDependantsByResidentId(residentId2)

        assertEquals(1, resident1Dependants.size)
        assertEquals(1, resident2Dependants.size)
        assertEquals(dependant1.id, resident1Dependants[0].id)
        assertEquals(dependant2.id, resident2Dependants[0].id)
    }

    private fun createTestDependant(residentId: UUID = UUID.randomUUID()): Dependant {
        return Dependant(
            id = UUID.randomUUID(),
            residentId = residentId,
            idNumber = "TEST123",
            name = "Test Name",
            surname = "Test Surname",
            gender = "M"
        )
    }
}
