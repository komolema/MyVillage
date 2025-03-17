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
        println("[DEBUG_LOG] Creating ManagedBy: $managedBy")

        val id = ManagedBy.insertAndGetId {
            it[resourceId] = managedBy.resourceId
            it[residentId] = managedBy.residentId
            it[status] = managedBy.status
            it[appointmentDate] = managedBy.appointmentDate
            it[position] = managedBy.position
        }

        println("[DEBUG_LOG] Inserted ManagedBy with id: ${id.value}")

        // Verify the record was inserted
        val query = """
            SELECT * FROM ManagedBy WHERE id = '${id.value}'
        """.trimIndent()

        println("[DEBUG_LOG] Verifying insertion with query: $query")

        exec(query) { rs ->
            if (rs.next()) {
                println("[DEBUG_LOG] Verified record exists with id: ${id.value}")
            } else {
                println("[DEBUG_LOG] WARNING: Record not found after insertion!")
            }
        }

        val result = managedBy.copy(id = id.value)
        println("[DEBUG_LOG] Returning: $result")
        result
    }

    override fun getManagedById(id: UUID): ManagedByModel? = transactionProvider.executeTransaction {
        println("[DEBUG_LOG] Getting ManagedBy with id: $id")

        // Use a raw SQL query to ensure we get all columns
        val query = """
            SELECT * FROM ManagedBy WHERE id = '$id'
        """.trimIndent()

        println("[DEBUG_LOG] Executing query: $query")

        val result = exec(query) { rs ->
            if (rs.next()) {
                val managedById = UUID.fromString(rs.getString("id"))
                val resourceId = UUID.fromString(rs.getString("resourceId"))
                val residentId = UUID.fromString(rs.getString("residentId"))
                val status = rs.getString("status")
                val appointmentDate = rs.getDate("appointmentDate").toLocalDate()
                val position = rs.getString("position")

                println("[DEBUG_LOG] Found row: id=$managedById, resourceId=$resourceId, status=$status")

                ManagedByModel(
                    id = managedById,
                    resourceId = resourceId,
                    residentId = residentId,
                    status = status,
                    appointmentDate = appointmentDate,
                    position = position
                )
            } else {
                println("[DEBUG_LOG] No row found for id: $id")
                null
            }
        }

        println("[DEBUG_LOG] Result: $result")
        result
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
        // Get the values if they exist, otherwise use defaults
        val id = if (this.hasValue(ManagedBy.id)) this[ManagedBy.id].value else UUID.randomUUID()
        val resourceId = if (this.hasValue(ManagedBy.resourceId)) this[ManagedBy.resourceId] else UUID.randomUUID()
        val residentId = if (this.hasValue(ManagedBy.residentId)) this[ManagedBy.residentId] else UUID.randomUUID()
        val status = if (this.hasValue(ManagedBy.status)) this[ManagedBy.status] else "Active"
        val appointmentDate = if (this.hasValue(ManagedBy.appointmentDate)) this[ManagedBy.appointmentDate] else java.time.LocalDate.now()
        val position = if (this.hasValue(ManagedBy.position)) this[ManagedBy.position] else "Manager"

        return ManagedByModel(
            id = id,
            resourceId = resourceId,
            residentId = residentId,
            status = status,
            appointmentDate = appointmentDate,
            position = position
        )
    }
}
