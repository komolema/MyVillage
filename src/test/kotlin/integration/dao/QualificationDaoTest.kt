package database.dao

import database.dao.domain.QualificationDaoImpl
import database.schema.domain.Qualifications
import models.domain.Qualification
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class QualificationDaoTest {

    private val qualificationDao = QualificationDaoImpl()

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(Qualifications)
        }
    }

    @Test
    fun testCreateQualification() {
        val qualification = createTestQualification()
        val createdQualification = qualificationDao.createQualification(qualification)
        assertNotNull(createdQualification.id)
    }

    @Test
    fun testGetQualificationsByResidentId() {
        val qualification = createTestQualification()
        qualificationDao.createQualification(qualification)
        val qualifications = qualificationDao.getQualificationsByResidentId(qualification.residentId)
        assertEquals(1, qualifications.size)
        assertEquals(qualification.name, qualifications[0].name)
    }

    @Test
    fun testUpdateQualification() {
        val qualification = createTestQualification()
        val createdQualification = qualificationDao.createQualification(qualification)
        val updatedQualification = createdQualification.copy(name = "Updated Qualification")
        val updateResult = qualificationDao.updateQualification(updatedQualification)
        assertTrue(updateResult)
        val fetchedQualification = qualificationDao.getQualificationsByResidentId(createdQualification.residentId).first()
        assertEquals("Updated Qualification", fetchedQualification.name)
    }

    @Test
    fun testDeleteQualification() {
        val qualification = createTestQualification()
        val createdQualification = qualificationDao.createQualification(qualification)
        val deleteResult = qualificationDao.deleteQualification(createdQualification.id)
        assertTrue(deleteResult)
        val qualifications = qualificationDao.getQualificationsByResidentId(createdQualification.residentId)
        assertTrue(qualifications.isEmpty())
    }

    private fun createTestQualification(name: String = "Test Qualification", institution: String = "Test Institution"): Qualification {
        return Qualification(
            id = UUID.randomUUID(),
            residentId = UUID.randomUUID(),
            name = name,
            institution = institution,
            startDate = LocalDate.parse("2023-01-01"),
            endDate = LocalDate.parse("2023-12-31"),
            nqfLevel = 5,
            city = "Test City"
        )
    }
}
