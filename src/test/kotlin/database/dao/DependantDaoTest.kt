package database.dao

import database.schema.Dependants
import database.schema.Residents
import models.Dependant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DependantDaoTest {
    private lateinit var dependantDao: DependantDao
    private lateinit var testResidentId: UUID
    
    @Before
    fun setUp() {
        // Initialize in-memory database for testing
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        
        // Initialize DAO
        dependantDao = DependantDaoImpl()
        
        // Set up database schema
        transaction {
            SchemaUtils.create(Residents, Dependants)
            
            // Create a test resident
            testResidentId = UUID.randomUUID()
            Residents.insert { row ->
                row[id] = testResidentId
                row[firstName] = "Test"
                row[lastName] = "Resident"
                row[dob] = LocalDate.now().minusYears(30)
                row[gender] = "Male"
                row[idNumber] = "TEST123"
                row[phoneNumber] = "1234567890"
                row[email] = "test@example.com"
            }
        }
    }
    
    @Test
    fun testGetDependantsByResidentId() {
        // Create test dependants
        val dependant1 = createTestDependant("DEP1", "John")
        val dependant2 = createTestDependant("DEP2", "Jane")
        
        // Test retrieving dependants
        val dependants = dependantDao.getDependantsByResidentId(testResidentId)
        
        // Verify results
        assertNotNull(dependants)
        assertEquals(2, dependants.size)
        assertEquals("John", dependants.find { it.idNumber == "DEP1" }?.name)
        assertEquals("Jane", dependants.find { it.idNumber == "DEP2" }?.name)
    }
    
    private fun createTestDependant(idNumber: String, name: String): Dependant {
        return transaction {
            dependantDao.createDependant(
                Dependant(
                    id = UUID.randomUUID(),
                    residentId = testResidentId,
                    idNumber = idNumber,
                    name = name,
                    surname = "Test",
                    gender = "Other"
                )
            )
        }
    }
}