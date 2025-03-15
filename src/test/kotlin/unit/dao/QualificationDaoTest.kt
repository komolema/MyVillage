package unit.dao

import database.dao.domain.QualificationDao
import database.dao.domain.QualificationDaoImpl
import database.schema.domain.Qualifications
import database.schema.domain.Residents
import models.domain.Qualification
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class QualificationDaoTest {
    private lateinit var qualificationDao: QualificationDao
    private lateinit var testResidentId: UUID

    @Before
    fun setUp() {
        // Initialize in-memory database for testing
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

        // Initialize DAO
        qualificationDao = QualificationDaoImpl()

        // Set up database schema
        transaction {
            SchemaUtils.drop(Qualifications, Residents)
            SchemaUtils.create(Residents, Qualifications)

            // Create a test resident with unique idNumber
            testResidentId = UUID.randomUUID()
            val uniqueIdNumber = "TEST${UUID.randomUUID().toString().take(8)}"

            Residents.insert { row ->
                row[id] = testResidentId
                row[firstName] = "Test"
                row[lastName] = "Resident"
                row[dob] = LocalDate.now().minusYears(30)
                row[gender] = "Male"
                row[idNumber] = uniqueIdNumber
                row[phoneNumber] = "1234567890"
                row[email] = "test@example.com"
            }
        }
    }

    @Test
    fun testGetQualificationsByResidentId_Success() {
        // Create test qualifications
        val qualification1 = createTestQualification("BSc Computer Science", "University A")
        val qualification2 = createTestQualification("MSc Data Science", "University B")

        // Test retrieving qualifications
        val qualifications = qualificationDao.getQualificationsByResidentId(testResidentId)

        // Verify results
        assertNotNull(qualifications)
        assertEquals(2, qualifications.size)
        assertEquals("BSc Computer Science", qualifications.find { it.institution == "University A" }?.name)
        assertEquals("MSc Data Science", qualifications.find { it.institution == "University B" }?.name)
    }

    @Test
    fun testGetQualificationsByResidentId_NonExistentResident() {
        // Test retrieving qualifications for non-existent resident
        val nonExistentResidentId = UUID.randomUUID()
        val qualifications = qualificationDao.getQualificationsByResidentId(nonExistentResidentId)

        // Verify results
        assertNotNull(qualifications)
        assertEquals(0, qualifications.size)
    }

    @Test
    fun testGetQualificationsByResidentId_EmptyQualifications() {
        // Test retrieving qualifications for resident with no qualifications
        val qualifications = qualificationDao.getQualificationsByResidentId(testResidentId)

        // Verify results
        assertNotNull(qualifications)
        assertEquals(0, qualifications.size)
    }

    @Test
    fun testGetQualificationsByResidentId_DeletedResident() {
        // Create a qualification for the resident
        val qualification = createTestQualification("BSc Computer Science", "University A")

        // Delete qualifications first, then the resident
        transaction {
            // Delete all qualifications for the resident
            Qualifications.deleteWhere { Qualifications.residentId eq testResidentId }
            // Now we can safely delete the resident
            Residents.deleteWhere { Residents.id eq testResidentId }
        }

        // Test retrieving qualifications
        val qualifications = qualificationDao.getQualificationsByResidentId(testResidentId)

        // Verify results
        assertNotNull(qualifications)
        assertEquals(0, qualifications.size)
    }

    private fun createTestQualification(name: String, institution: String): Qualification {
        return transaction {
            qualificationDao.createQualification(
                Qualification(
                    id = UUID.randomUUID(),
                    residentId = testResidentId,
                    name = name,
                    institution = institution,
                    startDate = LocalDate.now().minusYears(4),
                    endDate = LocalDate.now().minusYears(1),
                    nqfLevel = 7,
                    city = "Test City"
                )
            )
        }
    }
}
