package database.dao.domain

import database.DomainTransactionProvider
import database.TransactionProvider
import database.schema.domain.Dependants
import models.domain.Dependant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

interface DependantDao {
    fun createDependant(dependant: Dependant): Dependant
    fun getDependantById(id: UUID): Dependant?
    fun getDependantsByResidentId(residentId: UUID): List<Dependant>
    fun getAllDependants(): List<Dependant>
    fun updateDependant(dependant: Dependant): Boolean
    fun deleteDependant(id: UUID): Boolean
}

class DependantDaoImpl(
    private val transactionProvider: TransactionProvider = DomainTransactionProvider
) : DependantDao {
    override fun createDependant(dependant: Dependant): Dependant = transactionProvider.executeTransaction {
        println("[DEBUG_LOG] Starting createDependant transaction")
        println("[DEBUG_LOG] Inserting new dependant with residentId: ${dependant.residentId}")
        Dependants.insert {
            it[id] = dependant.id
            it[residentId] = dependant.residentId
            it[idNumber] = dependant.idNumber
            it[name] = dependant.name
            it[surname] = dependant.surname
            it[gender] = dependant.gender
        }
        println("[DEBUG_LOG] Insert successful, id: ${dependant.id}")

        dependant
    }

    override fun getDependantById(id: UUID): Dependant? = transactionProvider.executeTransaction {
        println("[DEBUG_LOG] Getting dependant by id: $id")
        Dependants.selectAll()
            .where { Dependants.id eq id }
            .limit(1)
            .map { row ->
                println("[DEBUG_LOG] Row data: id=${row[Dependants.id]}, name=${row[Dependants.name]}")
                row.toDependant()
            }.singleOrNull()
    }

    override fun getDependantsByResidentId(residentId: UUID): List<Dependant> = transactionProvider.executeTransaction {
        println("[DEBUG_LOG] Querying dependants for residentId: $residentId")
        Dependants.selectAll()
            .where { Dependants.residentId eq residentId }
            .map { row ->
                println("[DEBUG_LOG] Row data: id=${row[Dependants.id]}, name=${row[Dependants.name]}")
                row.toDependant()
            }
    }

    override fun getAllDependants(): List<Dependant> = transactionProvider.executeTransaction {
        println("[DEBUG_LOG] Getting all dependants")
        Dependants.selectAll()
            .map { row ->
                println("[DEBUG_LOG] Row data: id=${row[Dependants.id]}, name=${row[Dependants.name]}")
                row.toDependant()
            }
    }

    override fun updateDependant(dependant: Dependant): Boolean = transactionProvider.executeTransaction {
        Dependants.update({ Dependants.id eq dependant.id }) {
            it[residentId] = dependant.residentId
            it[idNumber] = dependant.idNumber
            it[name] = dependant.name
            it[surname] = dependant.surname
            it[gender] = dependant.gender
        } > 0
    }

    override fun deleteDependant(id: UUID): Boolean = transactionProvider.executeTransaction {
        Dependants.deleteWhere { Dependants.id eq id } > 0
    }

    private fun ResultRow.toDependant(): Dependant {
        val entityId = this[Dependants.id] as EntityID<UUID>
        return Dependant(
            id = entityId.value,
            residentId = this[Dependants.residentId],
            idNumber = this[Dependants.idNumber],
            name = this[Dependants.name],
            surname = this[Dependants.surname],
            gender = this[Dependants.gender]
        ).also {
            println("[DEBUG_LOG] Converting row to Dependant: id=${entityId.value}, residentId=${this[Dependants.residentId]}")
        }
    }
}
