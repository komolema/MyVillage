package database.dao

import arrow.core.toOption
import database.schema.Residents
import models.Resident
import models.expanded.ResidentExpanded
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class ResidentDao(private val residenceDao: ResidenceDao, private val dependantDao: DependantDao) {

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

    fun getResidentById(id: UUID): Resident? = transaction {
        Residents.select(Residents.id eq id)
            .mapNotNull { it.toResident() }
            .singleOrNull()
    }

    fun getAllResidentExpanded(page: Int, pageSize: Int): List<ResidentExpanded> = transaction {
        Residents.selectAll()
            .limit(pageSize).offset(start = (page * pageSize).toLong())
            .map { it.toResidentExpanded() }
    }

    fun getResidentExpandedById(id: UUID): ResidentExpanded? = transaction {
        Residents.select(Residents.id eq id)
            .mapNotNull { it.toResidentExpanded() }
            .singleOrNull()
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

    private fun ResultRow.toResidentExpanded(): ResidentExpanded {
        val resident = this.toResident()
        val address = residenceDao.getAddressByResidentId(resident.id).toOption()
        val residence = residenceDao.getResidenceByResidentId(resident.id).toOption()
        val dependents = dependantDao.getDependentsByResidentId(resident.id)
        return ResidentExpanded(
            resident = resident,
            address = address,
            residence = residence,
            dependants = dependents
        )
    }

}