package database.dao.domain

import arrow.core.toOption
import database.DomainTransactionProvider
import database.TransactionProvider
import database.schema.domain.*
import models.domain.Resident
import models.expanded.ResidentExpanded
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.dao.id.EntityID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

interface ResidentDao {
    fun getAll(page: Int, pageSize: Int): List<Resident>
    fun search(query: String, page: Int, pageSize: Int): List<Resident>
    suspend fun searchExpanded(query: String, page: Int, pageSize: Int): List<ResidentExpanded>
    fun getResidentById(id: UUID): Resident?
    suspend fun getAllResidentExpanded(page: Int, pageSize: Int): List<ResidentExpanded>
    suspend fun getResidentExpandedById(id: UUID): ResidentExpanded?
    fun createResident(residentState: Resident)
    fun updateResident(residentState: Resident)
    fun delete(id: UUID)
}

class ResidentDaoImpl(
    private val residenceDao: ResidenceDao, 
    private val dependantDao: DependantDao,
    private val transactionProvider: TransactionProvider = DomainTransactionProvider
) : ResidentDao {

    override fun getAll(page: Int, pageSize: Int): List<Resident> = transactionProvider.executeTransaction {
        Residents.selectAll()
            .limit(pageSize).offset(start = (page * pageSize).toLong())
            .map { it.toResident() }
    }

    private fun Transaction.searchResidents(searchTerm: String, page: Int, pageSize: Int): Query {
        return Residents.selectAll()
            .andWhere { 
                (Residents.firstName like "%$searchTerm%") or
                (Residents.lastName like "%$searchTerm%") or
                (Residents.idNumber like "%$searchTerm%")
            }
            .limit(pageSize)
            .offset(start = (page * pageSize).toLong())
    }

    override fun search(query: String, page: Int, pageSize: Int): List<Resident> = transactionProvider.executeTransaction {
        searchResidents(query, page, pageSize).map { it.toResident() }
    }

    override suspend fun searchExpanded(query: String, page: Int, pageSize: Int): List<ResidentExpanded> = 
        withContext(Dispatchers.IO) {
            val rows = transactionProvider.executeTransaction {
                searchResidents(query, page, pageSize).toList()
            }
            rows.map { row -> row.toResidentExpanded() }
        }

    override fun getResidentById(id: UUID): Resident? = transactionProvider.executeTransaction {
        Residents.selectAll()
            .andWhere { Residents.id eq id }
            .mapNotNull { it.toResident() }
            .singleOrNull()
    }

    override suspend fun getAllResidentExpanded(page: Int, pageSize: Int): List<ResidentExpanded> = 
        withContext(Dispatchers.IO) {
            val rows = transactionProvider.executeTransaction {
                Residents.selectAll()
                    .limit(pageSize).offset(start = (page * pageSize).toLong())
                    .toList()
            }
            rows.map { row -> row.toResidentExpanded() }
        }

    override suspend fun getResidentExpandedById(id: UUID): ResidentExpanded? = 
        withContext(Dispatchers.IO) {
            val row = transactionProvider.executeTransaction {
                Residents.selectAll()
                    .andWhere { Residents.id eq id }
                    .firstOrNull()
            }
            row?.let { it.toResidentExpanded() }
        }

    override fun createResident(residentState: Resident) {
        transactionProvider.executeTransaction {
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
        transactionProvider.executeTransaction {
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

    override fun delete(id: UUID) {
        transactionProvider.executeTransaction {
            // Delete qualifications first
            Qualifications.deleteWhere { Qualifications.residentId eq id }

            // Delete dependants
            Dependants.deleteWhere { Dependants.residentId eq id }

            // Get residence to find address
            val residence = Residences.select ( Residences.residentId eq id )
                .map { row -> row[Residences.addressId] }
                .singleOrNull()

            // Delete residence
            Residences.deleteWhere { Residences.residentId eq id }

            // Delete address if found
            if (residence != null) {
                Addresses.deleteWhere { Addresses.id eq residence }
            }

            // Finally delete resident
            Residents.deleteWhere { Residents.id eq id }
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

    private suspend fun ResultRow.toResidentExpanded(): ResidentExpanded {
        val resident = this.toResident()
        return withContext(Dispatchers.IO) {
            val address = residenceDao.getAddressByResidentId(resident.id).toOption()
            val residence = residenceDao.getResidenceByResidentId(resident.id).toOption()
            val dependants = dependantDao.getDependantsByResidentId(resident.id)
            ResidentExpanded(
                resident = resident,
                address = address,
                residence = residence,
                dependants = dependants
            )
        }
    }

    suspend fun mapResidentToExpanded(resident: Resident): ResidentExpanded =
        withContext(Dispatchers.IO) {
            val address = residenceDao.getAddressByResidentId(resident.id).toOption()
            val residence = residenceDao.getResidenceByResidentId(resident.id).toOption()
            val dependants = dependantDao.getDependantsByResidentId(resident.id)
            ResidentExpanded(
                resident = resident,
                address = address,
                residence = residence,
                dependants = dependants
            )
        }

}
