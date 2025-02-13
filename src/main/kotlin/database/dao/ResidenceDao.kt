package database.dao

import database.schema.Addresses
import database.schema.Residences
import models.Address
import models.Residence
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class ResidenceDao {

    fun getResidenceByResidentId(residentId: UUID): Residence? = transaction {
        Residences.select ( Residences.residentId eq residentId )
            .mapNotNull { it.toResidence() }
            .singleOrNull()
    }

    private fun ResultRow.toResidence(): Residence {
        return Residence(
            id = this[Residences.id].value,
            residentId = this[Residences.residentId],
            addressId = this[Residences.addressId],
            occupationDate = this[Residences.occupationDate]
        )
    }

    fun getAddressByResidentId(residentId: UUID): Address? = transaction {
        (Residences innerJoin Addresses).select ( Residences.residentId eq residentId )
            .mapNotNull { it.toAddress() }
            .singleOrNull()
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