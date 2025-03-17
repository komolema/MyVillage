package database.dao.domain

import database.DomainTransactionProvider
import database.TransactionProvider
import database.schema.domain.Leadership
import models.domain.Leadership as LeadershipModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

interface LeadershipDao {
    fun createLeadership(leadership: LeadershipModel): LeadershipModel
    fun getLeadershipById(id: UUID): LeadershipModel?
    fun getAllLeadership(): List<LeadershipModel>
    fun getLeadershipByVillage(villageName: String): List<LeadershipModel>
    fun updateLeadership(leadership: LeadershipModel): Boolean
    fun deleteLeadership(id: UUID): Boolean
}

class LeadershipDaoImpl(
    private val transactionProvider: TransactionProvider = DomainTransactionProvider
) : LeadershipDao {
    override fun createLeadership(leadership: LeadershipModel): LeadershipModel = transactionProvider.executeTransaction {
        val id = Leadership.insertAndGetId {
            it[name] = leadership.name
            it[role] = leadership.role
            it[startDate] = leadership.startDate
            it[endDate] = leadership.endDate
            it[villageName] = leadership.villageName
        }
        leadership.copy(id = id.value)
    }

    override fun getLeadershipById(id: UUID): LeadershipModel? = transactionProvider.executeTransaction {
        Leadership.select(Leadership.id eq id)
            .map { it.toLeadershipModel() }
            .singleOrNull()
    }

    override fun getAllLeadership(): List<LeadershipModel> = transactionProvider.executeTransaction {
        Leadership.selectAll()
            .map { it.toLeadershipModel() }
    }

    override fun getLeadershipByVillage(villageName: String): List<LeadershipModel> = transactionProvider.executeTransaction {
        Leadership.select(Leadership.villageName eq villageName)
            .map { it.toLeadershipModel() }
    }

    override fun updateLeadership(leadership: LeadershipModel): Boolean = transactionProvider.executeTransaction {
        Leadership.update({ Leadership.id eq leadership.id }) {
            it[name] = leadership.name
            it[role] = leadership.role
            it[startDate] = leadership.startDate
            it[endDate] = leadership.endDate
            it[villageName] = leadership.villageName
        } > 0
    }

    override fun deleteLeadership(id: UUID): Boolean = transactionProvider.executeTransaction {
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
