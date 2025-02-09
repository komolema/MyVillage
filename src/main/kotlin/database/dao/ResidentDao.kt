package database.dao

import database.schema.Residents
import models.Resident
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like


class ResidentDao {
    fun getAll(page: Int, pageSize: Int): List<Resident> = transaction {
        Residents.selectAll()
            .limit(pageSize).offset(start = (page * pageSize).toLong())
            .map { it.toResident() }
    }

    fun search(query: String, page: Int, pageSize: Int): List<Resident> = transaction {
        Residents.select(
            (Residents.idNumber like "%$query%") or
                    (Residents.firstName like "%$query%") or
                    (Residents.lastName like "%$query%")
        )
            .limit(pageSize).offset(start = (page * pageSize).toLong())
            .map { it.toResident() }
    }

    private fun ResultRow.toResident(): Resident {
        return Resident(
            id = this[Residents.id].value,
            idNumber = this[Residents.idNumber],
            firstName = this[Residents.firstName],
            lastName = this[Residents.lastName],
            dob = this[Residents.dob],
            gender = this[Residents.gender],
            phoneNumber = this[Residents.phoneNumber],
            email = this[Residents.email],
        )
    }
}