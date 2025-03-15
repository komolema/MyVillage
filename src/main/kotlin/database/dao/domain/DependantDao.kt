package database.dao.domain

import database.schema.domain.Dependants
import models.domain.Dependant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

interface DependantDao {
    fun createDependant(dependant: Dependant): Dependant
    fun getDependantById(id: UUID): Dependant?
    fun getDependantsByResidentId(residentId: UUID): List<Dependant>
    fun getAllDependants(): List<Dependant>
    fun updateDependant(dependant: Dependant): Boolean
    fun deleteDependant(id: UUID): Boolean
}

class DependantDaoImpl : DependantDao {
    override fun createDependant(dependant: Dependant): Dependant = transaction {
        println("[DEBUG_LOG] Starting createDependant transaction")
        println("[DEBUG_LOG] Verifying database state...")

        // Verify the Dependants table exists and is accessible
        try {
            val tableExists = Dependants.exists()
            println("[DEBUG_LOG] Dependants table exists: $tableExists")

            if (!tableExists) {
                println("[DEBUG_LOG] Attempting to create Dependants table")
                SchemaUtils.create(Dependants)
            }
        } catch (e: Exception) {
            println("[DEBUG_LOG] Error checking table existence: ${e.message}")
            throw e
        }

        println("[DEBUG_LOG] Inserting new dependant with residentId: ${dependant.residentId}")
        val id = Dependants.insertAndGetId {
            it[residentId] = dependant.residentId
            it[idNumber] = dependant.idNumber
            it[name] = dependant.name
            it[surname] = dependant.surname
            it[gender] = dependant.gender
        }
        println("[DEBUG_LOG] Insert successful, new id: ${id.value}")

        dependant.copy(id = id.value)
    }

    override fun getDependantById(id: UUID): Dependant? = transaction {
        println("[DEBUG_LOG] Getting dependant by id: $id")
        val query = Dependants.select ( Dependants.id eq id )
        println("[DEBUG_LOG] SQL: ${query.prepareSQL(this)}")
        query.map { row ->
            println("[DEBUG_LOG] Row data: id=${row[Dependants.id]}, name=${row[Dependants.name]}")
            row.toDependant()
        }.singleOrNull()
    }

    override fun getDependantsByResidentId(residentId: UUID): List<Dependant> = transaction {
        println("[DEBUG_LOG] Querying dependants for residentId: $residentId")
        val query = Dependants.selectAll().where { Dependants.residentId eq residentId }
        println("[DEBUG_LOG] SQL: ${query.prepareSQL(this)}")
        query.map { row ->
            println("[DEBUG_LOG] Row data: id=${row[Dependants.id]}, name=${row[Dependants.name]}")
            row.toDependant()
        }
    }

    override fun getAllDependants(): List<Dependant> = transaction {
        println("[DEBUG_LOG] Getting all dependants")
        val query = Dependants.selectAll()
        println("[DEBUG_LOG] SQL: ${query.prepareSQL(this)}")
        query.map { row ->
            println("[DEBUG_LOG] Row data: id=${row[Dependants.id]}, name=${row[Dependants.name]}")
            row.toDependant()
        }
    }

    override fun updateDependant(dependant: Dependant): Boolean = transaction {
        Dependants.update({ Dependants.id eq dependant.id }) {
            it[residentId] = dependant.residentId
            it[idNumber] = dependant.idNumber
            it[name] = dependant.name
            it[surname] = dependant.surname
            it[gender] = dependant.gender
        } > 0
    }

    override fun deleteDependant(id: UUID): Boolean = transaction {
        Dependants.deleteWhere { Dependants.id eq id } > 0
    }

    private fun ResultRow.toDependant(): Dependant {
        return Dependant(
            id = this[Dependants.id].value,
            residentId = this[Dependants.residentId],
            idNumber = this[Dependants.idNumber],
            name = this[Dependants.name],
            surname = this[Dependants.surname],
            gender = this[Dependants.gender]
        ).also {
            println("[DEBUG_LOG] Converting row to Dependant: id=${this[Dependants.id]}, residentId=${this[Dependants.residentId]}")
        }
    }
}
