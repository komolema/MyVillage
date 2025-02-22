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

interface ResidentDao {
    fun getAll(page: Int, pageSize: Int): List<Resident>
    fun search(query: String, page: Int, pageSize: Int): List<Resident>
    fun searchExpanded(query: String, page: Int, pageSize: Int): List<ResidentExpanded>
    fun getResidentById(id: UUID): Resident?
    fun getAllResidentExpanded(page: Int, pageSize: Int): List<ResidentExpanded>
    fun getResidentExpandedById(id: UUID): ResidentExpanded?
    fun createResident(residentState: Resident)
    fun updateResident(residentState: Resident)
}

class ResidentDaoImpl(private val residenceDao: ResidenceDao, private val dependantDao: DependantDao) : ResidentDao {

    override fun getAll(page: Int, pageSize: Int): List<Resident> = transaction {
        Residents.selectAll()
            .limit(pageSize).offset(start = (page * pageSize).toLong())
            .map { it.toResident() }
    }

    private fun searchResidents(query: String, page: Int, pageSize: Int): Query {
        return Residents.select(
            (Residents.idNumber like "%$query%") or
                    (Residents.firstName like "%$query%") or
                    (Residents.lastName like "%$query%")
        )
            .limit(pageSize).offset(start = (page * pageSize).toLong())
    }

    override fun search(query: String, page: Int, pageSize: Int): List<Resident> = transaction {
        searchResidents(query, page, pageSize).map { it.toResident() }
    }

    override fun searchExpanded(query: String, page: Int, pageSize: Int): List<ResidentExpanded> = transaction {
        searchResidents(query, page, pageSize).map { it.toResidentExpanded() }
    }

    override fun getResidentById(id: UUID): Resident? = transaction {
        Residents.select(Residents.id eq id)
            .mapNotNull { it.toResident() }
            .singleOrNull()
    }

    override fun getAllResidentExpanded(page: Int, pageSize: Int): List<ResidentExpanded> = transaction {
        Residents.selectAll()
            .limit(pageSize).offset(start = (page * pageSize).toLong())
            .map { it.toResidentExpanded() }
    }

    override fun getResidentExpandedById(id: UUID): ResidentExpanded? = transaction {
        Residents.select(Residents.id eq id)
            .mapNotNull { it.toResidentExpanded() }
            .singleOrNull()
    }

    override fun createResident(residentState: Resident) {
        transaction {
            Residents.insert {
                it[id] = residentState.id
                it[idNumber] = residentState.idNumber
                it[firstName] = residentState.firstName
                it[lastName] = residentState.lastName
                it[dob] = residentState.dob
                it[email] = residentState.email
                it[phoneNumber] = residentState.phoneNumber
                it[gender] = residentState.gender

            }
        }
    }

    override fun updateResident(residentState: Resident) {
        transaction {
            Residents.update({ Residents.id eq residentState.id }) {
                it[idNumber] = residentState.idNumber
                it[firstName] = residentState.firstName
                it[lastName] = residentState.lastName
                it[dob] = residentState.dob
                it[email] = residentState.email
                it[phoneNumber] = residentState.phoneNumber
                it[gender] = residentState.gender
            }
        }
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

    fun mapResidentToExpanded(resident: Resident): ResidentExpanded {
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