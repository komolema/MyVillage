package database.dao

import database.schema.Residents
import models.Resident
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ResidentDao {
    fun getAll(page: Int, pageSize: Int): List<Resident> = transaction {
        Residents.selectAll().limit(pageSize, offset = (page * pageSize).toLong()).map { it.toResident() }
    }

    fun search(query: String, page: Int, pageSize: Int): List<Resident> = transaction {
        Residents.select {
            Residents.idNumber like "%$query%" or
                    Residents.firstName like "%$query%" or
                    Residents.lastName like "%$query%"
        }.limit(pageSize, offset = (page * pageSize).toLong()).map { it.toResident() }
    }
}