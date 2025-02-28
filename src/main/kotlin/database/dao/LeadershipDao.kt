package database.dao

import database.schema.Leadership
import models.Leadership as LeadershipModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

interface LeadershipDao {
    fun createLeadership(leadership: LeadershipModel): LeadershipModel
    fun getLeadershipById(id: UUID): LeadershipModel?
    fun getAllLeadership(): List<LeadershipModel>
    fun getLeadershipByVillage(villageName: String): List<LeadershipModel>
    fun updateLeadership(leadership: LeadershipModel): Boolean
    fun deleteLeadership(id: UUID): Boolean
}

class LeadershipDaoImpl : LeadershipDao {
    override fun createLeadership(leadership: LeadershipModel): LeadershipModel = transaction {
        val id = Leadership.insertAndGetId {
            it[name] = leadership.name
            it[role] = leadership.role
            it[startDate] = leadership.startDate
            it[endDate] = leadership.endDate
            it[villageName] = leadership.villageName
        }
        leadership.copy(id = id.value)
    }

    override fun getLeadershipById(id: UUID): LeadershipModel? = transaction {
        Leadership.select(Leadership.id eq id)
            .map { it.toLeadershipModel() }
            .singleOrNull()
    }

    override fun getAllLeadership(): List<LeadershipModel> = transaction {
        Leadership.selectAll()
            .map { it.toLeadershipModel() }
    }

    override fun getLeadershipByVillage(villageName: String): List<LeadershipModel> = transaction {
        Leadership.select(Leadership.villageName eq villageName)
            .map { it.toLeadershipModel() }
    }

    override fun updateLeadership(leadership: LeadershipModel): Boolean = transaction {
        Leadership.update({ Leadership.id eq leadership.id }) {
            it[name] = leadership.name
            it[role] = leadership.role
            it[startDate] = leadership.startDate
            it[endDate] = leadership.endDate
            it[villageName] = leadership.villageName
        } > 0
    }

    override fun deleteLeadership(id: UUID): Boolean = transaction {
        Leadership.deleteWhere { Leadership.id eq id } > 0
    }

    private fun ResultRow.toLeadershipModel(): LeadershipModel {
        return LeadershipModel(
            id = this[Leadership.id].value,
            name = this[Leadership.name],
            role = this[Leadership.role],
            startDate = this[Leadership.startDate],
            endDate = this[Leadership.endDate],
            villageName = this[Leadership.villageName]
        )
    }
}
