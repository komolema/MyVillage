package database.dao.domain

import database.DomainTransactionProvider
import database.TransactionProvider
import database.schema.domain.ManagedBy
import models.domain.ManagedBy as ManagedByModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

interface ManagedByDao {
    fun createManagedBy(managedBy: ManagedByModel): ManagedByModel
    fun getManagedById(id: UUID): ManagedByModel?
    fun getAllManagedBy(): List<ManagedByModel>
    fun getManagedByResource(resourceId: UUID): List<ManagedByModel>
    fun getManagedByResident(residentId: UUID): List<ManagedByModel>
    fun updateManagedBy(managedBy: ManagedByModel): Boolean
    fun deleteManagedBy(id: UUID): Boolean

    companion object {
        private val impl = ManagedByDaoImpl()

        fun createManagedBy(managedBy: ManagedByModel): ManagedByModel = 
            impl.createManagedBy(managedBy)
        fun getManagedById(id: UUID): ManagedByModel? = 
            impl.getManagedById(id)
        fun getAllManagedBy(): List<ManagedByModel> = 
            impl.getAllManagedBy()
        fun getManagedByResource(resourceId: UUID): List<ManagedByModel> = 
            impl.getManagedByResource(resourceId)
        fun getManagedByResident(residentId: UUID): List<ManagedByModel> = 
            impl.getManagedByResident(residentId)
        fun updateManagedBy(managedBy: ManagedByModel): Boolean = 
            impl.updateManagedBy(managedBy)
        fun deleteManagedBy(id: UUID): Boolean = 
            impl.deleteManagedBy(id)
    }
}

class ManagedByDaoImpl(private val transactionProvider: TransactionProvider = DomainTransactionProvider) : ManagedByDao {
    override fun createManagedBy(managedBy: ManagedByModel): ManagedByModel = transactionProvider.executeTransaction {
        val id = ManagedBy.insertAndGetId {
            it[resourceId] = managedBy.resourceId
            it[residentId] = managedBy.residentId
            it[status] = managedBy.status
            it[appointmentDate] = managedBy.appointmentDate
            it[position] = managedBy.position
        }
        managedBy.copy(id = id.value)
    }

    override fun getManagedById(id: UUID): ManagedByModel? = transactionProvider.executeTransaction {
        ManagedBy.select(ManagedBy.id eq id)
            .map { it.toManagedByModel() }
            .singleOrNull()
    }

    override fun getAllManagedBy(): List<ManagedByModel> = transactionProvider.executeTransaction {
        ManagedBy.selectAll()
            .map { it.toManagedByModel() }
    }

    override fun getManagedByResource(resourceId: UUID): List<ManagedByModel> = transactionProvider.executeTransaction {
        ManagedBy.select(ManagedBy.resourceId eq resourceId)
            .map { it.toManagedByModel() }
    }

    override fun getManagedByResident(residentId: UUID): List<ManagedByModel> = transactionProvider.executeTransaction {
        ManagedBy.select(ManagedBy.residentId eq residentId)
            .map { it.toManagedByModel() }
    }

    override fun updateManagedBy(managedBy: ManagedByModel): Boolean = transactionProvider.executeTransaction {
        ManagedBy.update({ ManagedBy.id eq managedBy.id }) {
            it[resourceId] = managedBy.resourceId
            it[residentId] = managedBy.residentId
            it[status] = managedBy.status
            it[appointmentDate] = managedBy.appointmentDate
            it[position] = managedBy.position
        } > 0
    }

    override fun deleteManagedBy(id: UUID): Boolean = transactionProvider.executeTransaction {
        ManagedBy.deleteWhere { ManagedBy.id eq id } > 0
    }

    private fun ResultRow.toManagedByModel(): ManagedByModel {
        return ManagedByModel(
            id = this[ManagedBy.id].value,
            resourceId = this[ManagedBy.resourceId],
            residentId = this[ManagedBy.residentId],
            status = this[ManagedBy.status],
            appointmentDate = this[ManagedBy.appointmentDate],
            position = this[ManagedBy.position]
        )
    }
}
