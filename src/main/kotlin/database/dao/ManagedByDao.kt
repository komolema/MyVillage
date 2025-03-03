package database.dao

import database.schema.ManagedBy
import models.ManagedBy as ManagedByModel
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

interface ManagedByDao {
    fun createManagedBy(managedBy: ManagedByModel): ManagedByModel
    fun getManagedById(id: UUID): ManagedByModel?
    fun getAllManagedBy(): List<ManagedByModel>
    fun getManagedByResource(resourceId: UUID): List<ManagedByModel>
    fun getManagedByResident(residentId: UUID): List<ManagedByModel>
    fun updateManagedBy(managedBy: ManagedByModel): Boolean
    fun deleteManagedBy(id: UUID): Boolean
}

class ManagedByDaoImpl : ManagedByDao {
    override fun createManagedBy(managedBy: ManagedByModel): ManagedByModel = transaction {
        val id = ManagedBy.insertAndGetId {
            it[resourceId] = managedBy.resourceId
            it[residentId] = managedBy.residentId
            it[status] = managedBy.status
            it[appointmentDate] = managedBy.appointmentDate
            it[position] = managedBy.position
        }
        managedBy.copy(id = id.value)
    }

    override fun getManagedById(id: UUID): ManagedByModel? = transaction {
        ManagedBy.select(ManagedBy.id eq id)
            .map { it.toManagedByModel() }
            .singleOrNull()
    }

    override fun getAllManagedBy(): List<ManagedByModel> = transaction {
        ManagedBy.selectAll()
            .map { it.toManagedByModel() }
    }

    override fun getManagedByResource(resourceId: UUID): List<ManagedByModel> = transaction {
        ManagedBy.select(ManagedBy.resourceId eq resourceId)
            .map { it.toManagedByModel() }
    }

    override fun getManagedByResident(residentId: UUID): List<ManagedByModel> = transaction {
        ManagedBy.select(ManagedBy.residentId eq residentId)
            .map { it.toManagedByModel() }
    }

    override fun updateManagedBy(managedBy: ManagedByModel): Boolean = transaction {
        ManagedBy.update({ ManagedBy.id eq managedBy.id }) {
            it[resourceId] = managedBy.resourceId
            it[residentId] = managedBy.residentId
            it[status] = managedBy.status
            it[appointmentDate] = managedBy.appointmentDate
            it[position] = managedBy.position
        } > 0
    }

    override fun deleteManagedBy(id: UUID): Boolean = transaction {
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
