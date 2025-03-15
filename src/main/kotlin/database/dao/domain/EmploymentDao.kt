package database.dao.domain

import database.schema.domain.EmploymentTable
import models.domain.Employment
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

interface EmploymentDao {
    fun createEmployment(employment: Employment): Employment
    fun getEmploymentByResidentId(residentId: UUID): List<Employment>
    fun updateEmployment(employment: Employment): Boolean
    fun deleteEmployment(employmentId: UUID): Boolean
}

class EmploymentDaoImpl : EmploymentDao {
    override fun createEmployment(employment: Employment): Employment = transaction {
        EmploymentTable.insert {
            it[id] = employment.id
            it[residentId] = employment.residentId
            it[employer] = employment.employer
            it[role] = employment.role
            it[startDate] = employment.startDate
            it[endDate] = employment.endDate
        }
        employment
    }

    override fun getEmploymentByResidentId(residentId: UUID): List<Employment> = transaction {
        EmploymentTable.selectAll()
            .where { EmploymentTable.residentId eq residentId }
            .map { row ->
                Employment(
                    id = row[EmploymentTable.id].value,
                    residentId = row[EmploymentTable.residentId],
                    employer = row[EmploymentTable.employer],
                    role = row[EmploymentTable.role],
                    startDate = row[EmploymentTable.startDate],
                    endDate = row[EmploymentTable.endDate]
                )
            }
    }

    override fun updateEmployment(employment: Employment): Boolean = transaction {
        val updatedRows = EmploymentTable.update({ EmploymentTable.id eq employment.id }) {
            it[employer] = employment.employer
            it[role] = employment.role
            it[startDate] = employment.startDate
            it[endDate] = employment.endDate
        }
        updatedRows > 0
    }

    override fun deleteEmployment(employmentId: UUID): Boolean = transaction {
        val deletedRows = EmploymentTable.deleteWhere { EmploymentTable.id eq employmentId }
        deletedRows > 0
    }
}
