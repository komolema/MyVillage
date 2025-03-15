package integration.dao

import database.dao.domain.ResourceDaoImpl
import database.schema.domain.Resources
import models.domain.Resource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.*

class ResourceDaoTest {
    private val resourceDao = ResourceDaoImpl()

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Resources)
        }
    }

    private fun createTestResource(type: String = "Test Type", location: String = "Test Location"): Resource {
        return Resource(
            id = UUID.randomUUID(),
            type = type,
            location = location
        )
    }

    @Test
    fun testCreateAndGetById() = transaction {
        val resource = createTestResource()
        resourceDao.create(resource)

        val fetchedResource = resourceDao.getById(resource.id)
        assertNotNull(fetchedResource)
        assertEquals(resource.type, fetchedResource?.type)
        assertEquals(resource.location, fetchedResource?.location)
    }

    @Test
    fun testGetAllWithPagination() = transaction {
        val resources = (1..15).map { 
            createTestResource("Type $it", "Location $it")
        }
        resources.forEach { resourceDao.create(it) }

        // Test first page
        val firstPage = resourceDao.getAll(page = 0, pageSize = 5)
        assertEquals(5, firstPage.size)

        // Test second page
        val secondPage = resourceDao.getAll(page = 1, pageSize = 5)
        assertEquals(5, secondPage.size)

        // Verify no overlap between pages
        val firstPageIds = firstPage.map { it.id }
        val secondPageIds = secondPage.map { it.id }
        assertEquals(0, firstPageIds.intersect(secondPageIds).size)
    }

    @Test
    fun testSearch() = transaction {
        val resources = listOf(
            createTestResource("Water Resource", "North Lake"),
            createTestResource("Land Resource", "South Field"),
            createTestResource("Forest Resource", "West Woods"),
            createTestResource("Water Resource", "East River")
        )
        resources.forEach { resourceDao.create(it) }

        // Test search by type
        val waterResources = resourceDao.search("Water", page = 0, pageSize = 10)
        assertEquals(2, waterResources.size)
        assertTrue(waterResources.all { it.type.contains("Water") })

        // Test search by location
        val northResources = resourceDao.search("North", page = 0, pageSize = 10)
        assertEquals(1, northResources.size)
        assertTrue(northResources.all { it.location.contains("North") })

        // Test pagination in search
        val allResources = resourceDao.search("Resource", page = 0, pageSize = 2)
        assertEquals(2, allResources.size)
    }

    @Test
    fun testUpdate() = transaction {
        val resource = createTestResource()
        resourceDao.create(resource)

        val updatedResource = resource.copy(
            type = "Updated Type",
            location = "Updated Location"
        )
        resourceDao.update(updatedResource)

        val fetchedResource = resourceDao.getById(resource.id)
        assertEquals(updatedResource.type, fetchedResource?.type)
        assertEquals(updatedResource.location, fetchedResource?.location)
    }

    @Test
    fun testDelete() = transaction {
        val resource = createTestResource()
        resourceDao.create(resource)

        resourceDao.delete(resource.id)

        val fetchedResource = resourceDao.getById(resource.id)
        assertNull(fetchedResource)
    }

    @Test
    fun testSearchWithEmptyResult() = transaction {
        val resources = listOf(
            createTestResource("Water Resource", "North Lake"),
            createTestResource("Land Resource", "South Field")
        )
        resources.forEach { resourceDao.create(it) }

        val noResults = resourceDao.search("NonExistent", page = 0, pageSize = 10)
        assertTrue(noResults.isEmpty())
    }

    @Test
    fun testPaginationWithEmptyPages() = transaction {
        val resources = (1..3).map { 
            createTestResource("Type $it", "Location $it")
        }
        resources.forEach { resourceDao.create(it) }

        val emptyPage = resourceDao.getAll(page = 2, pageSize = 2)
        assertTrue(emptyPage.isEmpty())
    }
}
