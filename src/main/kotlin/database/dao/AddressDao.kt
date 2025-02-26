package database.dao

import database.schema.Addresses
import models.Address
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Data Access Object interface for managing Address entities.
 * Provides CRUD operations, search functionality, and pagination support.
 */
interface AddressDao {
    /**
     * Retrieves a paginated list of all addresses.
     *
     * @param page The page number (0-based)
     * @param pageSize The number of items per page
     * @return List of addresses for the specified page
     */
    fun getAll(page: Int, pageSize: Int): List<Address>

    /**
     * Searches for addresses matching the given query string.
     * Searches across line, suburb, town, and postal code fields.
     *
     * @param query The search query string
     * @param page The page number (0-based)
     * @param pageSize The number of items per page
     * @return List of matching addresses
     */
    fun search(query: String, page: Int, pageSize: Int): List<Address>

    /**
     * Retrieves an address by its unique identifier.
     *
     * @param id The UUID of the address
     * @return The address if found, null otherwise
     */
    fun getById(id: UUID): Address?

    /**
     * Retrieves an address by its associated resident ID.
     *
     * @param residentId The UUID of the resident
     * @return The address if found, null otherwise
     */
    fun getByResidentId(residentId: UUID): Address?

    /**
     * Creates a new address record.
     *
     * @param address The address to create
     */
    fun create(address: Address)

    /**
     * Updates an existing address record.
     *
     * @param address The address with updated information
     */
    fun update(address: Address)

    /**
     * Deletes an address by its unique identifier.
     *
     * @param id The UUID of the address to delete
     */
    fun delete(id: UUID)
}

class AddressDaoImpl : AddressDao {
    override fun getAll(page: Int, pageSize: Int): List<Address> = transaction {
        Addresses.selectAll()
            .limit(pageSize)
            .offset(start = (page * pageSize).toLong())
            .map { it.toAddress() }
    }

    override fun search(query: String, page: Int, pageSize: Int): List<Address> = transaction {
        Addresses.select(
            (Addresses.line like "%$query%") or
            (Addresses.suburb like "%$query%") or
            (Addresses.town like "%$query%") or
            (Addresses.postalCode like "%$query%")
        )
            .limit(pageSize)
            .offset(start = (page * pageSize).toLong())
            .map { it.toAddress() }
    }

    override fun getById(id: UUID): Address? = transaction {
        Addresses.select(Addresses.id eq id)
            .mapNotNull { it.toAddress() }
            .singleOrNull()
    }

    override fun getByResidentId(residentId: UUID): Address? = transaction {
        Addresses.select(Addresses.id eq residentId)
            .mapNotNull { it.toAddress() }
            .singleOrNull()
    }

    override fun create(address: Address): Unit = transaction {
        Addresses.insert {
            it[id] = address.id
            it[line] = address.line
            it[houseNumber] = address.houseNumber
            it[suburb] = address.suburb
            it[town] = address.town
            it[postalCode] = address.postalCode
            it[geoCoordinates] = address.geoCoordinates
            it[landmark] = address.landmark
        }
        Unit
    }

    override fun update(address: Address): Unit = transaction {
        Addresses.update({ Addresses.id eq address.id }) {
            it[line] = address.line
            it[houseNumber] = address.houseNumber
            it[suburb] = address.suburb
            it[town] = address.town
            it[postalCode] = address.postalCode
            it[geoCoordinates] = address.geoCoordinates
            it[landmark] = address.landmark
        }
        Unit
    }

    override fun delete(id: UUID): Unit = transaction {
        Addresses.deleteWhere { Addresses.id eq id }
        Unit
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
