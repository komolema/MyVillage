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
    fun testCreateDependant() {
        val dependant = createTestDependant()
        dependantDao.create(dependant)
        val fetchedDependant = dependantDao.getById(dependant.id)
        assertNotNull(fetchedDependant)
        assertEquals(dependant.name, fetchedDependant?.name)
        assertEquals(dependant.surname, fetchedDependant?.surname)
    }

    @Test
    fun testGetDependantById() {
        val dependant = createTestDependant()
        dependantDao.create(dependant)
        val fetchedDependant = dependantDao.getById(dependant.id)
        assertNotNull(fetchedDependant)
        assertEquals(dependant.id, fetchedDependant?.id)
        assertEquals(dependant.name, fetchedDependant?.name)
        assertEquals(dependant.surname, fetchedDependant?.surname)
    }

    @Test
    fun testUpdateDependant() {
        val dependant = createTestDependant()
        dependantDao.create(dependant)

        val updatedDependant = dependant.copy(
            name = "Updated Name",
            surname = "Updated Surname"
        )
        dependantDao.update(updatedDependant)

        val fetchedDependant = dependantDao.getById(dependant.id)
        assertNotNull(fetchedDependant)
        assertEquals("Updated Name", fetchedDependant?.name)
        assertEquals("Updated Surname", fetchedDependant?.surname)
    }

    @Test
    fun testDeleteDependant() {
        val dependant = createTestDependant()
        dependantDao.create(dependant)

        dependantDao.delete(dependant.id)

        val fetchedDependant = dependantDao.getById(dependant.id)
        assertNull(fetchedDependant)
    }

    @Test
    fun testGetDependentsByResidentId() {
        val residentId = UUID.randomUUID()
        val dependant1 = createTestDependant(residentId)
        val dependant2 = createTestDependant(residentId)
        val dependant3 = createTestDependant(UUID.randomUUID()) // Different resident

        dependantDao.create(dependant1)
        dependantDao.create(dependant2)
        dependantDao.create(dependant3)

        val dependants = dependantDao.getDependentsByResidentId(residentId)
        assertEquals(2, dependants.size)
        assertTrue(dependants.any { it.id == dependant1.id })
        assertTrue(dependants.any { it.id == dependant2.id })
        assertFalse(dependants.any { it.id == dependant3.id })
    }

    @Test
    fun testSearchDependant() {
        val dependant1 = createTestDependant()
        val dependant2 = createTestDependant().copy(
            id = UUID.randomUUID(),
            name = "Different Name",
            surname = "Different Surname"
        )

        dependantDao.create(dependant1)
        dependantDao.create(dependant2)

        val searchResults = dependantDao.search("Test", 0, 10)
        assertTrue(searchResults.any { it.id == dependant1.id })
        assertFalse(searchResults.any { it.id == dependant2.id })
    }

    @Test
    fun testPagination() {
        val dependants = (1..5).map {
            createTestDependant().copy(
                id = UUID.randomUUID(),
                name = "Name $it"
            )
        }
        dependants.forEach { dependantDao.create(it) }

        val page1 = dependantDao.getAll(0, 2)
        val page2 = dependantDao.getAll(1, 2)
        val page3 = dependantDao.getAll(2, 2)

        assertEquals(2, page1.size)
        assertEquals(2, page2.size)
        assertEquals(1, page3.size)
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
