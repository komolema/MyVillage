package database.dao

import database.schema.Jobs
import models.Job
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

interface EmploymentDao {
    fun createJob(job: Job): Job
    fun getJobsByResidentId(residentId: UUID): List<Job>
    fun updateJob(job: Job): Boolean
    fun deleteJob(jobId: UUID): Boolean
}

class EmploymentDaoImpl : EmploymentDao {
    override fun createJob(job: Job): Job = transaction {
        Jobs.insert {
            it[id] = job.id
            it[residentId] = job.residentId
            it[employer] = job.employer
            it[role] = job.role
            it[startDate] = job.startDate
            it[endDate] = job.endDate
        }
        job
    }

    override fun getJobsByResidentId(residentId: UUID): List<Job> = transaction {
        Jobs.selectAll()
            .where { Jobs.residentId eq residentId }
            .map { row ->
                Job(
                    id = row[Jobs.id].value,
                    residentId = row[Jobs.residentId],
                    employer = row[Jobs.employer],
                    role = row[Jobs.role],
                    startDate = row[Jobs.startDate],
                    endDate = row[Jobs.endDate]
                )
            }
    }

    override fun updateJob(job: Job): Boolean = transaction {
        val updatedRows = Jobs.update({ Jobs.id eq job.id }) {
            it[employer] = job.employer
            it[role] = job.role
            it[startDate] = job.startDate
            it[endDate] = job.endDate
        }
        updatedRows > 0
    }

    override fun deleteJob(jobId: UUID): Boolean = transaction {
        val deletedRows = Jobs.deleteWhere { Jobs.id eq jobId }
        deletedRows > 0
    }
}
