package database.dao

import database.schema.Jobs
import models.Job
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
            SchemaUtils.create(Jobs)
        }
    }

    private fun createTestJob(residentId: UUID = UUID.randomUUID()): Job {
        return Job(
            id = UUID.randomUUID(),
            residentId = residentId,
            employer = "Test Company",
            role = "Software Engineer",
            startDate = LocalDate.now().minusYears(1),
            endDate = LocalDate.now()
        )
    }

    @Test
    fun testCreateJob() = transaction {
        val job = createTestJob()
        val createdJob = employmentDao.createJob(job)
        assertNotNull(createdJob.id)
        assertEquals(job.employer, createdJob.employer)
        assertEquals(job.role, createdJob.role)
    }

    @Test
    fun testGetJobsByResidentId() = transaction {
        val residentId = UUID.randomUUID()
        val jobs = listOf(
            createTestJob(residentId).copy(role = "Developer"),
            createTestJob(residentId).copy(role = "Manager"),
            createTestJob(residentId).copy(role = "Architect")
        )
        jobs.forEach { employmentDao.createJob(it) }

        val fetchedJobs = employmentDao.getJobsByResidentId(residentId)
        assertEquals(jobs.size, fetchedJobs.size)
        jobs.forEach { job ->
            assertTrue(fetchedJobs.any { it.id == job.id && it.role == job.role })
        }
    }

    @Test
    fun testUpdateJob() = transaction {
        val job = createTestJob()
        val createdJob = employmentDao.createJob(job)

        val updatedJob = createdJob.copy(
            employer = "Updated Company",
            role = "Senior Engineer"
        )

        val updateResult = employmentDao.updateJob(updatedJob)
        assertTrue(updateResult)

        val fetchedJobs = employmentDao.getJobsByResidentId(job.residentId)
        assertEquals(1, fetchedJobs.size)
        assertEquals(updatedJob.employer, fetchedJobs[0].employer)
        assertEquals(updatedJob.role, fetchedJobs[0].role)
    }

    @Test
    fun testDeleteJob() = transaction {
        val job = createTestJob()
        val createdJob = employmentDao.createJob(job)

        val deleteResult = employmentDao.deleteJob(createdJob.id)
        assertTrue(deleteResult)

        val fetchedJobs = employmentDao.getJobsByResidentId(job.residentId)
        assertTrue(fetchedJobs.isEmpty())
    }
}
