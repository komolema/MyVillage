package integration.dao

import database.TestTransactionProvider
import database.dao.domain.ManagedByDao
import database.dao.domain.ManagedByDaoImpl
import database.schema.domain.ManagedBy
import database.schema.domain.Resources
import database.schema.domain.Residents
import models.domain.ManagedBy as ManagedByModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.util.*
import kotlin.io.createTempFile

class ManagedByDaoTest {
    // Fixed UUIDs for testing
    private val testResourceId = UUID.fromString("00000000-0000-0000-0000-000000000001")
    private val testResidentId = UUID.fromString("00000000-0000-0000-0000-000000000002")
    
    private lateinit var testTransactionProvider: TestTransactionProvider
    private lateinit var managedByDao: ManagedByDaoImpl
    
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
                SchemaUtils.create(ManagedBy, Resources, Residents)
                commit()
                
                // Verify schema creation
                val tableExists = exec("SELECT name FROM sqlite_master WHERE type='table' AND name='ManagedBy'") { 
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
            exec("DELETE FROM ManagedBy")
            exec("DELETE FROM Resources")
            exec("DELETE FROM Residents")
            commit()
            println("[DEBUG_LOG] Tables cleared")
            
            // Create test resources and residents with fixed IDs
            Resources.insert {
                it[id] = testResourceId
                it[type] = "Test Resource"
                it[location] = "Test Location"
            }
            
            Residents.insert {
                it[id] = testResidentId
                it[firstName] = "Test"
                it[lastName] = "Resident"
                it[dob] = LocalDate.now().minusYears(30)
                it[gender] = "Male"
                it[idNumber] = "1234567890"
            }
            
            commit()
        }
        
        // Initialize the DAO with the TestTransactionProvider
        testTransactionProvider = TestTransactionProvider(db)
        managedByDao = ManagedByDaoImpl(testTransactionProvider)
    }
    
    private fun createTestManagedBy(
        resourceId: UUID = testResourceId,
        residentId: UUID = testResidentId
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
        val createdManagedBy = managedByDao.createManagedBy(managedBy)

        assertNotNull(createdManagedBy.id)
        assertEquals(managedBy.resourceId, createdManagedBy.resourceId)
        assertEquals(managedBy.residentId, createdManagedBy.residentId)
        assertEquals(managedBy.status, createdManagedBy.status)
        assertEquals(managedBy.position, createdManagedBy.position)
    }

    @Test
    fun testGetManagedById() {
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
    fun testGetAllManagedBy() {
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
    fun testGetManagedByResource() {
        val resourceId = UUID.randomUUID()
        val managedByList = listOf(
            createTestManagedBy(resourceId = resourceId),
            createTestManagedBy(resourceId = resourceId),
            createTestManagedBy()
        )
        managedByList.forEach { managedByDao.createManagedBy(it) }

        val fetchedManagedBy = managedByDao.getManagedByResource(resourceId)
        assertEquals(2, fetchedManagedBy.size)
        fetchedManagedBy.forEach { item -> 
            assertEquals(resourceId, item.resourceId)
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
        managedByList.forEach { managedByDao.createManagedBy(it) }

        val fetchedManagedBy = managedByDao.getManagedByResident(residentId)
        assertEquals(2, fetchedManagedBy.size)
        fetchedManagedBy.forEach { item -> 
            assertEquals(residentId, item.residentId)
        }
    }

    @Test
    fun testUpdateManagedBy() {
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
    fun testDeleteManagedBy() {
        val managedBy = createTestManagedBy()
        val createdManagedBy = managedByDao.createManagedBy(managedBy)

        val deleteResult = managedByDao.deleteManagedBy(createdManagedBy.id)
        assertTrue(deleteResult)

        val fetchedManagedBy = managedByDao.getManagedById(createdManagedBy.id)
        assertNull(fetchedManagedBy)
    }
}