package integration.dao

import database.dao.domain.OwnershipDao
import database.schema.domain.Ownerships
import database.schema.domain.Animals
import database.schema.domain.Residents
import database.schema.domain.Payments
import models.domain.Ownership
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class OwnershipDaoTest {
    @BeforeEach
    fun setup() {
        Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Ownerships, Animals, Residents, Payments)
        }
    }

    private fun createTestOwnership(
        residentId: UUID = UUID.randomUUID(),
        animalId: UUID = UUID.randomUUID(),
        valid: Boolean = true
    ): Ownership {
        return Ownership(
            id = UUID.randomUUID(),
            residentId = residentId,
            animalId = animalId,
            paymentId = null,
            valid = valid,
            acquisitionDate = LocalDate.now(),
            acquisitionMethod = "Purchase",
            ownershipType = "Full",
            sharedWith = null
        )
    }

    @Test
    fun testCreateOwnership() {
        val ownership = createTestOwnership()
        val createdOwnership = OwnershipDao.createOwnership(ownership)

        assertNotNull(createdOwnership.id)
        assertEquals(ownership.residentId, createdOwnership.residentId)
        assertEquals(ownership.animalId, createdOwnership.animalId)
        assertEquals(ownership.valid, createdOwnership.valid)
        assertEquals(ownership.acquisitionMethod, createdOwnership.acquisitionMethod)

        val fetchedOwnership = OwnershipDao.getOwnershipById(createdOwnership.id)
        assertNotNull(fetchedOwnership)
        assertEquals(createdOwnership.ownershipType, fetchedOwnership?.ownershipType)
    }

    @Test
    fun testGetOwnershipById() {
        val ownership = createTestOwnership()
        val createdOwnership = OwnershipDao.createOwnership(ownership)

        val fetchedOwnership = OwnershipDao.getOwnershipById(createdOwnership.id)
        assertNotNull(fetchedOwnership)
        assertEquals(createdOwnership.residentId, fetchedOwnership?.residentId)
        assertEquals(createdOwnership.animalId, fetchedOwnership?.animalId)
        assertEquals(createdOwnership.valid, fetchedOwnership?.valid)
    }

    @Test
    fun testGetAllOwnerships() {
        val ownerships = listOf(
            createTestOwnership(),
            createTestOwnership(),
            createTestOwnership()
        )
        ownerships.forEach { OwnershipDao.createOwnership(it) }

        val fetchedOwnerships = OwnershipDao.getAllOwnerships()
        assertEquals(ownerships.size, fetchedOwnerships.size)
    }

    @Test
    fun testGetOwnershipsByResident() {
        val residentId = UUID.randomUUID()
        val ownerships = listOf(
            createTestOwnership(residentId = residentId),
            createTestOwnership(residentId = residentId),
            createTestOwnership() // Different resident
        )
        ownerships.forEach { OwnershipDao.createOwnership(it) }

        val fetchedOwnerships = OwnershipDao.getOwnershipsByResident(residentId)
        assertEquals(2, fetchedOwnerships.size)
        fetchedOwnerships.forEach { 
            assertEquals(residentId, it.residentId)
        }
    }

    @Test
    fun testGetOwnershipsByAnimal() {
        val animalId = UUID.randomUUID()
        val ownerships = listOf(
            createTestOwnership(animalId = animalId),
            createTestOwnership(animalId = animalId),
            createTestOwnership() // Different animal
        )
        ownerships.forEach { OwnershipDao.createOwnership(it) }

        val fetchedOwnerships = OwnershipDao.getOwnershipsByAnimal(animalId)
        assertEquals(2, fetchedOwnerships.size)
        fetchedOwnerships.forEach { 
            assertEquals(animalId, it.animalId)
        }
    }

    @Test
    fun testGetValidOwnerships() {
        val ownerships = listOf(
            createTestOwnership(valid = true),
            createTestOwnership(valid = true),
            createTestOwnership(valid = false)
        )
        ownerships.forEach { OwnershipDao.createOwnership(it) }

        val fetchedOwnerships = OwnershipDao.getValidOwnerships()
        assertEquals(2, fetchedOwnerships.size)
        fetchedOwnerships.forEach { 
            assertTrue(it.valid)
        }
    }

    @Test
    fun testUpdateOwnership() {
        val ownership = createTestOwnership()
        val createdOwnership = OwnershipDao.createOwnership(ownership)

        val updatedOwnership = createdOwnership.copy(
            valid = false,
            ownershipType = "Shared",
            sharedWith = "User1,User2"
        )

        val updateResult = OwnershipDao.updateOwnership(updatedOwnership)
        assertTrue(updateResult)

        val fetchedOwnership = OwnershipDao.getOwnershipById(createdOwnership.id)
        assertNotNull(fetchedOwnership)
        assertEquals(updatedOwnership.valid, fetchedOwnership?.valid)
        assertEquals(updatedOwnership.ownershipType, fetchedOwnership?.ownershipType)
        assertEquals(updatedOwnership.sharedWith, fetchedOwnership?.sharedWith)
    }

    @Test
    fun testDeleteOwnership() {
        val ownership = createTestOwnership()
        val createdOwnership = OwnershipDao.createOwnership(ownership)

        val deleteResult = OwnershipDao.deleteOwnership(createdOwnership.id)
        assertTrue(deleteResult)

        val fetchedOwnership = OwnershipDao.getOwnershipById(createdOwnership.id)
        assertNull(fetchedOwnership)
    }
}
