package integration.dao

import database.TestTransactionProvider
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

/**
 * A simplified test for ManagedByDao that focuses on basic CRUD operations.
 */
class SimpleManagedByDaoTest {
    private lateinit var db: Database
    private lateinit var managedByDao: ManagedByDaoImpl

    // Store resource and resident IDs for use in tests
    private lateinit var testResourceId: UUID
    private lateinit var testResidentId: UUID

    @BeforeEach
    fun setup() {
        // Create a new in-memory database for each test
        db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

        // Create the schema
        transaction(db) {
            SchemaUtils.create(ManagedBy, Resources, Residents)

            // Create test data with fixed IDs
            testResourceId = UUID.randomUUID()
            Resources.insert {
                it[id] = testResourceId
                it[type] = "Test Resource"
                it[location] = "Test Location"
            }

            testResidentId = UUID.randomUUID()
            Residents.insert {
                it[id] = testResidentId
                it[firstName] = "Test"
                it[lastName] = "Resident"
                it[dob] = LocalDate.now().minusYears(30)
                it[gender] = "Male"
                it[idNumber] = "1234567890"
            }

            commit()
            println("[DEBUG_LOG] Schema created and test data inserted")
            println("[DEBUG_LOG] Test resource ID: $testResourceId")
            println("[DEBUG_LOG] Test resident ID: $testResidentId")
        }

        // Initialize the DAO with the test database
        val testTransactionProvider = TestTransactionProvider(db)
        managedByDao = ManagedByDaoImpl(testTransactionProvider)
    }

    @Test
    fun testSimpleCreateAndGet() {
        // First, let's check the actual column names in the database
        transaction(db) {
            // Check the table structure
            val columns = exec("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'MANAGEDBY'") { rs ->
                val cols = mutableListOf<String>()
                while (rs.next()) {
                    cols.add(rs.getString("COLUMN_NAME"))
                }
                cols
            }
            println("[DEBUG_LOG] Actual column names in ManagedBy table: $columns")

            // Check if the table exists
            val tables = exec("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'") { rs ->
                val tableNames = mutableListOf<String>()
                while (rs.next()) {
                    tableNames.add(rs.getString("TABLE_NAME"))
                }
                tableNames
            }
            println("[DEBUG_LOG] Tables in database: $tables")
        }

        // Create a test ManagedBy
        val managedBy = ManagedByModel(
            id = UUID.randomUUID(),
            resourceId = UUID.randomUUID(),
            residentId = UUID.randomUUID(),
            status = "Active",
            appointmentDate = LocalDate.now(),
            position = "Manager"
        )

        println("[DEBUG_LOG] Test ManagedBy created: $managedBy")

        // Insert using Exposed DSL
        transaction(db) {
            ManagedBy.insert {
                it[ManagedBy.id] = managedBy.id
                it[ManagedBy.resourceId] = managedBy.resourceId
                it[ManagedBy.residentId] = managedBy.residentId
                it[ManagedBy.status] = managedBy.status
                it[ManagedBy.appointmentDate] = managedBy.appointmentDate
                it[ManagedBy.position] = managedBy.position
            }
            commit()

            // Verify it was inserted
            val count = ManagedBy.selectAll().count()
            println("[DEBUG_LOG] Number of records in ManagedBy: $count")
        }

        // Try to retrieve it using the DAO
        val retrieved = managedByDao.getManagedById(managedBy.id)
        println("[DEBUG_LOG] Retrieved: $retrieved")

        // Assert that it was retrieved correctly
        assertNotNull(retrieved)
        assertEquals(managedBy.id, retrieved?.id)
        assertEquals(managedBy.resourceId, retrieved?.resourceId)
        assertEquals(managedBy.status, retrieved?.status)
    }
}
