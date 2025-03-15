package database.dao.domain

import database.schema.domain.Addresses
import database.schema.domain.Residences
import models.domain.Address
import models.domain.Residence
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
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

class ResidenceDaoImpl : ResidenceDao {

    override fun getResidenceByResidentId(residentId: UUID): Residence? = transaction {
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

    override fun getAddressByResidentId(residentId: UUID): Address? = transaction {
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

    override fun getAllResidences(): List<Residence> = transaction {
        Residences.selectAll()
            .map { it.toResidence() }
    }

    override fun getResidenceById(id: UUID): Residence? = transaction {
        Residences.selectAll()
            .andWhere { Residences.id eq id }
            .mapNotNull { it.toResidence() }
            .singleOrNull()
    }

    override fun createResidence(residence: Residence): Residence = transaction {
        val id = Residences.insertAndGetId {
            it[residentId] = residence.residentId
            it[addressId] = residence.addressId
            it[occupationDate] = residence.occupationDate
        }
        residence.copy(id = id.value)
    }

    override fun updateResidence(residence: Residence): Boolean = transaction {
        Residences.update({ Residences.id eq residence.id }) {
            it[residentId] = residence.residentId
            it[addressId] = residence.addressId
            it[occupationDate] = residence.occupationDate
        } > 0
    }

    override fun deleteResidence(id: UUID): Boolean = transaction {
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
