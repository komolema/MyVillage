package database.dao

import database.schema.Dependants
import models.Dependant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

interface DependantDao {
    fun getDependentsByResidentId(residentId: UUID): List<Dependant>
}
class DependantDaoImpl : DependantDao {
    override fun getDependentsByResidentId(residentId: UUID): List<Dependant> = transaction {
        Dependants.select (Dependants.residentId eq residentId )
            .map { it.toDependant() }
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