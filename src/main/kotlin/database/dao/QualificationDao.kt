package database.dao

import database.schema.Qualifications
import models.Qualification
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

interface QualificationDao {
    fun getQualificationsByResidentId(residentId: UUID): List<Qualification>
    fun createQualification(qualification: Qualification): Qualification
    fun updateQualification(qualification: Qualification): Boolean
    fun deleteQualification(id: UUID): Boolean
}

class QualificationDaoImpl : QualificationDao {
    override fun getQualificationsByResidentId(residentId: UUID): List<Qualification> = transaction {
        println("[DEBUG_LOG] Querying qualifications for residentId: $residentId")
        val query = Qualifications.selectAll().where { Qualifications.residentId eq residentId }
        println("[DEBUG_LOG] SQL: ${query.prepareSQL(this)}")
        query.map { row ->
            println("[DEBUG_LOG] Row data: id=${row[Qualifications.id]}, name=${row[Qualifications.name]}")
            row.toQualification()
        }
    }

    override fun createQualification(qualification: Qualification): Qualification = transaction {
        val id = Qualifications.insertAndGetId {
            it[residentId] = qualification.residentId
            it[name] = qualification.name
            it[institution] = qualification.institution
            it[startDate] = qualification.startDate
            it[endDate] = qualification.endDate
            it[nqfLevel] = qualification.nqfLevel
            it[city] = qualification.city
        }
        qualification.copy(id = id.value)
    }

    override fun updateQualification(qualification: Qualification): Boolean = transaction {
        Qualifications.update({ Qualifications.id eq qualification.id }) {
            it[residentId] = qualification.residentId
            it[name] = qualification.name
            it[institution] = qualification.institution
            it[startDate] = qualification.startDate
            it[endDate] = qualification.endDate
            it[nqfLevel] = qualification.nqfLevel
            it[city] = qualification.city
        } > 0
    }

    override fun deleteQualification(id: UUID): Boolean = transaction {
        Qualifications.deleteWhere { Qualifications.id eq id } > 0
    }

    private fun ResultRow.toQualification(): Qualification {
        return Qualification(
            id = this[Qualifications.id].value,
            residentId = this[Qualifications.residentId],
            name = this[Qualifications.name],
            institution = this[Qualifications.institution],
            startDate = this[Qualifications.startDate],
            endDate = this[Qualifications.endDate],
            nqfLevel = this[Qualifications.nqfLevel],
            city = this[Qualifications.city],
        )
    }
}
