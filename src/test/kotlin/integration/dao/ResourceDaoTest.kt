package integration.dao

import database.dao.ResourceDaoImpl
import database.schema.Resources
import models.Resource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Integration tests for ResourceDao implementation.
 * Tests CRUD operations, search functionality, and pagination.
 *
 * Uses an in-memory SQLite database that is recreated for each test.
 * Tests focus on resource type and location management.
 */
class ResourceDaoTest {
    private val resourceDao = ResourceDaoImpl()

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Resources)
        }
    }

    @Test
    fun testCreateResource() {
        val resource = createTestResource()
        resourceDao.create(resource)
        val fetchedResource = resourceDao.getById(resource.id)
        assertNotNull(fetchedResource)
        assertEquals(resource.type, fetchedResource?.type)
        assertEquals(resource.location, fetchedResource?.location)
    }

    @Test
    fun testGetResourceById() {
        val resource = createTestResource()
        resourceDao.create(resource)
        val fetchedResource = resourceDao.getById(resource.id)
        assertNotNull(fetchedResource)
        assertEquals(resource.id, fetchedResource?.id)
        assertEquals(resource.type, fetchedResource?.type)
        assertEquals(resource.location, fetchedResource?.location)
    }

    @Test
    fun testUpdateResource() {
        val resource = createTestResource()
        resourceDao.create(resource)

        val updatedResource = resource.copy(
            type = "Updated Type",
            location = "Updated Location"
        )
        resourceDao.update(updatedResource)

        val fetchedResource = resourceDao.getById(resource.id)
        assertNotNull(fetchedResource)
        assertEquals("Updated Type", fetchedResource?.type)
        assertEquals("Updated Location", fetchedResource?.location)
    }

    @Test
    fun testDeleteResource() {
        val resource = createTestResource()
        resourceDao.create(resource)

        resourceDao.delete(resource.id)

        val fetchedResource = resourceDao.getById(resource.id)
        assertNull(fetchedResource)
    }

    @Test
    fun testSearchResource() {
        val resource1 = createTestResource()
        val resource2 = createTestResource().copy(
            id = UUID.randomUUID(),
            type = "Different Type",
            location = "Different Location"
        )

        resourceDao.create(resource1)
        resourceDao.create(resource2)

        val searchResults = resourceDao.search("Test", 0, 10)
        assertTrue(searchResults.any { it.id == resource1.id })
        assertFalse(searchResults.any { it.id == resource2.id })
    }

    @Test
    fun testPagination() {
        val resources = (1..5).map {
            createTestResource().copy(
                id = UUID.randomUUID(),
                type = "Type $it"
            )
        }
        resources.forEach { resourceDao.create(it) }

        val page1 = resourceDao.getAll(0, 2)
        val page2 = resourceDao.getAll(1, 2)
        val page3 = resourceDao.getAll(2, 2)

        assertEquals(2, page1.size)
        assertEquals(2, page2.size)
        assertEquals(1, page3.size)
    }

    private fun createTestResource(): Resource {
        return Resource(
            id = UUID.randomUUID(),
            type = "Test Type",
            location = "Test Location"
        )
    }
}
