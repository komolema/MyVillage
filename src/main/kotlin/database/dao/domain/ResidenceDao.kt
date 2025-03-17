package database.dao.domain

import database.DomainTransactionProvider
import database.TransactionProvider
import database.schema.domain.Addresses
import database.schema.domain.Residences
import models.domain.Address
import models.domain.Residence
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

interface ResidenceDao {
    fun getResidenceByResidentId(residentId: UUID): Residence?
    fun getAddressByResidentId(residentId: UUID): Address?
    fun getAllResidences(): List<Residence>
    fun getResidenceById(id: UUID): Residence?
    fun createResidence(residence: Residence): Residence
    fun updateResidence(residence: Residence): Boolean
    fun deleteResidence(id: UUID): Boolean
}

class ResidenceDaoImpl(
    private val transactionProvider: TransactionProvider = DomainTransactionProvider
) : ResidenceDao {

    override fun getResidenceByResidentId(residentId: UUID): Residence? = transactionProvider.executeTransaction {
        Residences.selectAll()
            .where { Residences.residentId eq residentId }
            .limit(1)
            .firstOrNull()?.let { row ->
                Residence(
                    id = row[Residences.id].value,
                    residentId = row[Residences.residentId],
                    addressId = row[Residences.addressId],
                    occupationDate = row[Residences.occupationDate]
                )
            }
    }

    override fun getAddressByResidentId(residentId: UUID): Address? = transactionProvider.executeTransaction {
        (Addresses innerJoin Residences)
            .selectAll()
            .where { Residences.residentId eq residentId }
            .andWhere { Residences.addressId eq Addresses.id }
            .limit(1)
            .firstOrNull()?.let { row ->
                Address(
                    id = row[Addresses.id].value,
                    line = row[Addresses.line],
                    houseNumber = row[Addresses.houseNumber],
                    suburb = row[Addresses.suburb],
                    town = row[Addresses.town],
                    postalCode = row[Addresses.postalCode],
                    geoCoordinates = row[Addresses.geoCoordinates],
                    landmark = row[Addresses.landmark]
                )
            }
    }

    override fun getAllResidences(): List<Residence> = transactionProvider.executeTransaction {
        Residences.selectAll()
            .map { it.toResidence() }
    }

    override fun getResidenceById(id: UUID): Residence? = transactionProvider.executeTransaction {
        Residences.selectAll()
            .andWhere { Residences.id eq id }
            .mapNotNull { it.toResidence() }
            .singleOrNull()
    }

    override fun createResidence(residence: Residence): Residence = transactionProvider.executeTransaction {
        Residences.insert {
            it[id] = residence.id
            it[residentId] = residence.residentId
            it[addressId] = residence.addressId
            it[occupationDate] = residence.occupationDate
        }
        residence
    }

    override fun updateResidence(residence: Residence): Boolean = transactionProvider.executeTransaction {
        Residences.update({ Residences.id eq residence.id }) {
            it[residentId] = residence.residentId
            it[addressId] = residence.addressId
            it[occupationDate] = residence.occupationDate
        } > 0
    }

    override fun deleteResidence(id: UUID): Boolean = transactionProvider.executeTransaction {
        Residences.deleteWhere { Residences.id eq id } > 0
    }

    private fun ResultRow.toResidence(): Residence {
        return Residence(
            id = this[Residences.id].value,
            residentId = this[Residences.residentId],
            addressId = this[Residences.addressId],
            occupationDate = this[Residences.occupationDate]
        )
    }

    private fun ResultRow.toAddress(): Address {
        return Address(
            id = this[Addresses.id].value,
            line = this[Addresses.line],
            houseNumber = this[Addresses.houseNumber],
            suburb = this[Addresses.suburb],
            town = this[Addresses.town],
            postalCode = this[Addresses.postalCode],
            geoCoordinates = this[Addresses.geoCoordinates],
            landmark = this[Addresses.landmark]
        )
    }
}
