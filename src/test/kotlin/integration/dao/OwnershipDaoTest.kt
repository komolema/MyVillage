package integration.dao

import database.dao.domain.OwnershipDaoImpl
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
    private val ownershipDao = OwnershipDaoImpl()

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
        transaction {
            val ownership = createTestOwnership()
            val createdOwnership = ownershipDao.createOwnership(ownership)

            assertNotNull(createdOwnership.id)
            assertEquals(ownership.residentId, createdOwnership.residentId)
            assertEquals(ownership.animalId, createdOwnership.animalId)
            assertEquals(ownership.valid, createdOwnership.valid)
            assertEquals(ownership.acquisitionMethod, createdOwnership.acquisitionMethod)

            val fetchedOwnership = ownershipDao.getOwnershipById(createdOwnership.id)
            assertNotNull(fetchedOwnership)
            assertEquals(createdOwnership.ownershipType, fetchedOwnership?.ownershipType)
        }
    }

    @Test
    fun testGetOwnershipById() {
        transaction {
            val ownership = createTestOwnership()
            val createdOwnership = ownershipDao.createOwnership(ownership)

            val fetchedOwnership = ownershipDao.getOwnershipById(createdOwnership.id)
            assertNotNull(fetchedOwnership)
            assertEquals(createdOwnership.residentId, fetchedOwnership?.residentId)
            assertEquals(createdOwnership.animalId, fetchedOwnership?.animalId)
            assertEquals(createdOwnership.valid, fetchedOwnership?.valid)
        }
    }

    @Test
    fun testGetAllOwnerships() {
        transaction {
            val ownerships = listOf(
                createTestOwnership(),
                createTestOwnership(),
                createTestOwnership()
            )
            ownerships.forEach { ownershipDao.createOwnership(it) }

            val fetchedOwnerships = ownershipDao.getAllOwnerships()
            assertEquals(ownerships.size, fetchedOwnerships.size)
        }
    }

    @Test
    fun testGetOwnershipsByResident() {
        transaction {
            val residentId = UUID.randomUUID()
            val ownerships = listOf(
                createTestOwnership(residentId = residentId),
                createTestOwnership(residentId = residentId),
                createTestOwnership() // Different resident
            )
            ownerships.forEach { ownershipDao.createOwnership(it) }

            val fetchedOwnerships = ownershipDao.getOwnershipsByResident(residentId)
            assertEquals(2, fetchedOwnerships.size)
            fetchedOwnerships.forEach { 
                assertEquals(residentId, it.residentId)
            }
        }
    }

    @Test
    fun testGetOwnershipsByAnimal() {
        transaction {
            val animalId = UUID.randomUUID()
            val ownerships = listOf(
                createTestOwnership(animalId = animalId),
                createTestOwnership(animalId = animalId),
                createTestOwnership() // Different animal
            )
            ownerships.forEach { ownershipDao.createOwnership(it) }

            val fetchedOwnerships = ownershipDao.getOwnershipsByAnimal(animalId)
            assertEquals(2, fetchedOwnerships.size)
            fetchedOwnerships.forEach { 
                assertEquals(animalId, it.animalId)
            }
        }
    }

    @Test
    fun testGetValidOwnerships() {
        transaction {
            val ownerships = listOf(
                createTestOwnership(valid = true),
                createTestOwnership(valid = true),
                createTestOwnership(valid = false)
            )
            ownerships.forEach { ownershipDao.createOwnership(it) }

            val fetchedOwnerships = ownershipDao.getValidOwnerships()
            assertEquals(2, fetchedOwnerships.size)
            fetchedOwnerships.forEach { 
                assertTrue(it.valid)
            }
        }
    }

    @Test
    fun testUpdateOwnership() {
        transaction {
            val ownership = createTestOwnership()
            val createdOwnership = ownershipDao.createOwnership(ownership)

            val updatedOwnership = createdOwnership.copy(
                valid = false,
                ownershipType = "Shared",
                sharedWith = "User1,User2"
            )

            val updateResult = ownershipDao.updateOwnership(updatedOwnership)
            assertTrue(updateResult)

            val fetchedOwnership = ownershipDao.getOwnershipById(createdOwnership.id)
            assertNotNull(fetchedOwnership)
            assertEquals(updatedOwnership.valid, fetchedOwnership?.valid)
            assertEquals(updatedOwnership.ownershipType, fetchedOwnership?.ownershipType)
            assertEquals(updatedOwnership.sharedWith, fetchedOwnership?.sharedWith)
        }
    }

    @Test
    fun testDeleteOwnership() {
        transaction {
            val ownership = createTestOwnership()
            val createdOwnership = ownershipDao.createOwnership(ownership)

            val deleteResult = ownershipDao.deleteOwnership(createdOwnership.id)
            assertTrue(deleteResult)

            val fetchedOwnership = ownershipDao.getOwnershipById(createdOwnership.id)
            assertNull(fetchedOwnership)
        }
    }
}
