package integration.dao

import database.dao.domain.ManagedByDao
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
    fun testCreateManagedBy() {
        val managedBy = createTestManagedBy()
        val createdManagedBy = ManagedByDao.createManagedBy(managedBy)

        assertNotNull(createdManagedBy.id)
        assertEquals(managedBy.resourceId, createdManagedBy.resourceId)
        assertEquals(managedBy.residentId, createdManagedBy.residentId)
        assertEquals(managedBy.status, createdManagedBy.status)
        assertEquals(managedBy.position, createdManagedBy.position)
    }

    @Test
    fun testGetManagedById() {
        val managedBy = createTestManagedBy()
        val createdManagedBy = ManagedByDao.createManagedBy(managedBy)

        val fetchedManagedBy = ManagedByDao.getManagedById(createdManagedBy.id)
        assertNotNull(fetchedManagedBy)
        assertEquals(createdManagedBy.resourceId, fetchedManagedBy?.resourceId)
        assertEquals(createdManagedBy.residentId, fetchedManagedBy?.residentId)
        assertEquals(createdManagedBy.status, fetchedManagedBy?.status)
        assertEquals(createdManagedBy.position, fetchedManagedBy?.position)
    }

    @Test
    fun testGetAllManagedBy() {
        val managedByList = listOf(
            createTestManagedBy(),
            createTestManagedBy(),
            createTestManagedBy()
        )
        managedByList.forEach { ManagedByDao.createManagedBy(it) }

        val fetchedManagedBy = ManagedByDao.getAllManagedBy()
        assertEquals(managedByList.size, fetchedManagedBy.size)
    }

    @Test
    fun testGetManagedByResource() {
        val resourceId = UUID.randomUUID()
        val managedByList = listOf(
            createTestManagedBy(resourceId = resourceId),
            createTestManagedBy(resourceId = resourceId),
            createTestManagedBy()
        )
        managedByList.forEach { ManagedByDao.createManagedBy(it) }

        val fetchedManagedBy = ManagedByDao.getManagedByResource(resourceId)
        assertEquals(2, fetchedManagedBy.size)
        fetchedManagedBy.forEach { 
            assertEquals(resourceId, it.resourceId)
        }
    }

    @Test
    fun testGetManagedByResident() {
        val residentId = UUID.randomUUID()
        val managedByList = listOf(
            createTestManagedBy(residentId = residentId),
            createTestManagedBy(residentId = residentId),
            createTestManagedBy()
        )
        managedByList.forEach { ManagedByDao.createManagedBy(it) }

        val fetchedManagedBy = ManagedByDao.getManagedByResident(residentId)
        assertEquals(2, fetchedManagedBy.size)
        fetchedManagedBy.forEach { 
            assertEquals(residentId, it.residentId)
        }
    }

    @Test
    fun testUpdateManagedBy() {
        val managedBy = createTestManagedBy()
        val createdManagedBy = ManagedByDao.createManagedBy(managedBy)

        val updatedManagedBy = createdManagedBy.copy(
            status = "Inactive",
            position = "Senior Manager"
        )

        val updateResult = ManagedByDao.updateManagedBy(updatedManagedBy)
        assertTrue(updateResult)

        val fetchedManagedBy = ManagedByDao.getManagedById(createdManagedBy.id)
        assertEquals(updatedManagedBy.status, fetchedManagedBy?.status)
        assertEquals(updatedManagedBy.position, fetchedManagedBy?.position)
    }

    @Test
    fun testDeleteManagedBy() {
        val managedBy = createTestManagedBy()
        val createdManagedBy = ManagedByDao.createManagedBy(managedBy)

        val deleteResult = ManagedByDao.deleteManagedBy(createdManagedBy.id)
        assertTrue(deleteResult)

        val fetchedManagedBy = ManagedByDao.getManagedById(createdManagedBy.id)
        assertNull(fetchedManagedBy)
    }
}
