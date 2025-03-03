package database.dao

import database.schema.Dependants
import models.Dependant
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
        val id = Dependants.insertAndGetId {
            it[residentId] = dependant.residentId
            it[idNumber] = dependant.idNumber
            it[name] = dependant.name
            it[surname] = dependant.surname
            it[gender] = dependant.gender
        }
        dependant.copy(id = id.value)
    }

    override fun getDependantById(id: UUID): Dependant? = transaction {
        Dependants.select(Dependants.id eq id)
            .map { it.toDependant() }
            .singleOrNull()
    }

    override fun getDependantsByResidentId(residentId: UUID): List<Dependant> = transaction {
        Dependants.select(Dependants.residentId eq residentId)
            .map { it.toDependant() }
    }

    override fun getAllDependants(): List<Dependant> = transaction {
        Dependants.selectAll()
            .map { it.toDependant() }
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
        )
    }
}
