package integration.dao

import database.dao.domain.ManagedByDaoImpl
import database.schema.domain.ManagedBy
import database.schema.domain.Resources
import database.schema.domain.Residents
import models.domain.ManagedBy as ManagedByModel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.util.*

class ManagedByDaoTest {
    private val managedByDao = ManagedByDaoImpl()

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(ManagedBy, Resources, Residents)
        }
    }

    private fun createTestManagedBy(
        resourceId: UUID = UUID.randomUUID(),
        residentId: UUID = UUID.randomUUID()
    ): ManagedByModel {
        return ManagedByModel(
            id = UUID.randomUUID(),
            resourceId = resourceId,
            residentId = residentId,
            status = "Active",
            appointmentDate = LocalDate.now(),
            position = "Manager"
        )
    }

    @Test
    fun testCreateManagedBy() = transaction {
        val managedBy = createTestManagedBy()
        val createdManagedBy = managedByDao.createManagedBy(managedBy)

        assertNotNull(createdManagedBy.id)
        assertEquals(managedBy.resourceId, createdManagedBy.resourceId)
        assertEquals(managedBy.residentId, createdManagedBy.residentId)
        assertEquals(managedBy.status, createdManagedBy.status)
        assertEquals(managedBy.position, createdManagedBy.position)
    }

    @Test
    fun testGetManagedById() = transaction {
        val managedBy = createTestManagedBy()
        val createdManagedBy = managedByDao.createManagedBy(managedBy)

        val fetchedManagedBy = managedByDao.getManagedById(createdManagedBy.id)
        assertNotNull(fetchedManagedBy)
        assertEquals(createdManagedBy.resourceId, fetchedManagedBy?.resourceId)
        assertEquals(createdManagedBy.residentId, fetchedManagedBy?.residentId)
        assertEquals(createdManagedBy.status, fetchedManagedBy?.status)
        assertEquals(createdManagedBy.position, fetchedManagedBy?.position)
    }

    @Test
    fun testGetAllManagedBy() = transaction {
        val managedByList = listOf(
            createTestManagedBy(),
            createTestManagedBy(),
            createTestManagedBy()
        )
        managedByList.forEach { managedByDao.createManagedBy(it) }

        val fetchedManagedBy = managedByDao.getAllManagedBy()
        assertEquals(managedByList.size, fetchedManagedBy.size)
    }

    @Test
    fun testGetManagedByResource() = transaction {
        val resourceId = UUID.randomUUID()
        val managedByList = listOf(
            createTestManagedBy(resourceId = resourceId),
            createTestManagedBy(resourceId = resourceId),
            createTestManagedBy()
        )
        managedByList.forEach { managedByDao.createManagedBy(it) }

        val fetchedManagedBy = managedByDao.getManagedByResource(resourceId)
        assertEquals(2, fetchedManagedBy.size)
        fetchedManagedBy.forEach { 
            assertEquals(resourceId, it.resourceId)
        }
    }

    @Test
    fun testGetManagedByResident() = transaction {
        val residentId = UUID.randomUUID()
        val managedByList = listOf(
            createTestManagedBy(residentId = residentId),
            createTestManagedBy(residentId = residentId),
            createTestManagedBy()
        )
        managedByList.forEach { managedByDao.createManagedBy(it) }

        val fetchedManagedBy = managedByDao.getManagedByResident(residentId)
        assertEquals(2, fetchedManagedBy.size)
        fetchedManagedBy.forEach { 
            assertEquals(residentId, it.residentId)
        }
    }

    @Test
    fun testUpdateManagedBy() = transaction {
        val managedBy = createTestManagedBy()
        val createdManagedBy = managedByDao.createManagedBy(managedBy)

        val updatedManagedBy = createdManagedBy.copy(
            status = "Inactive",
            position = "Senior Manager"
        )

        val updateResult = managedByDao.updateManagedBy(updatedManagedBy)
        assertTrue(updateResult)

        val fetchedManagedBy = managedByDao.getManagedById(createdManagedBy.id)
        assertEquals(updatedManagedBy.status, fetchedManagedBy?.status)
        assertEquals(updatedManagedBy.position, fetchedManagedBy?.position)
    }

    @Test
    fun testDeleteManagedBy() = transaction {
        val managedBy = createTestManagedBy()
        val createdManagedBy = managedByDao.createManagedBy(managedBy)

        val deleteResult = managedByDao.deleteManagedBy(createdManagedBy.id)
        assertTrue(deleteResult)

        val fetchedManagedBy = managedByDao.getManagedById(createdManagedBy.id)
        assertNull(fetchedManagedBy)
    }
}
