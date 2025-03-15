package integration.dao

import database.schema.domain.EmploymentTable
import models.domain.Employment
import database.dao.domain.EmploymentDaoImpl
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.util.*

class EmploymentDaoTest {
    private val employmentDao = EmploymentDaoImpl()

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(EmploymentTable)
        }
    }

    private fun createTestEmployment(residentId: UUID = UUID.randomUUID()): Employment {
        return Employment(
            id = UUID.randomUUID(),
            residentId = residentId,
            employer = "Test Company",
            role = "Software Engineer",
            startDate = LocalDate.now().minusYears(1),
            endDate = LocalDate.now()
        )
    }

    @Test
    fun testCreateEmployment() = transaction {
        val employment = createTestEmployment()
        val createdEmployment = employmentDao.createEmployment(employment)
        assertNotNull(createdEmployment.id)
        assertEquals(employment.employer, createdEmployment.employer)
        assertEquals(employment.role, createdEmployment.role)
    }

    @Test
    fun testGetEmploymentByResidentId() = transaction {
        val residentId = UUID.randomUUID()
        val employments = listOf(
            createTestEmployment(residentId).copy(role = "Developer"),
            createTestEmployment(residentId).copy(role = "Manager"),
            createTestEmployment(residentId).copy(role = "Architect")
        )
        employments.forEach { employmentDao.createEmployment(it) }

        val fetchedEmployments = employmentDao.getEmploymentByResidentId(residentId)
        assertEquals(employments.size, fetchedEmployments.size)
        employments.forEach { employment ->
            assertTrue(fetchedEmployments.any { it.id == employment.id && it.role == employment.role })
        }
    }

    @Test
    fun testUpdateEmployment() = transaction {
        val employment = createTestEmployment()
        val createdEmployment = employmentDao.createEmployment(employment)

        val updatedEmployment = createdEmployment.copy(
            employer = "Updated Company",
            role = "Senior Engineer"
        )

        val updateResult = employmentDao.updateEmployment(updatedEmployment)
        assertTrue(updateResult as Boolean)

        val fetchedEmployments = employmentDao.getEmploymentByResidentId(employment.residentId)
        assertEquals(1, fetchedEmployments.size)
        assertEquals(updatedEmployment.employer, fetchedEmployments[0].employer)
        assertEquals(updatedEmployment.role, fetchedEmployments[0].role)
    }

    @Test
    fun testDeleteEmployment() = transaction {
        val employment = createTestEmployment()
        val createdEmployment = employmentDao.createEmployment(employment)

        val deleteResult = employmentDao.deleteEmployment(createdEmployment.id)
        assertTrue(deleteResult as Boolean)

        val fetchedEmployments = employmentDao.getEmploymentByResidentId(employment.residentId)
        assertTrue(fetchedEmployments.isEmpty())
    }
}
