package database.dao.domain

import database.DomainTransactionProvider
import database.TransactionProvider
import database.schema.domain.Addresses
import models.domain.Address
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.dao.id.EntityID
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

class AddressDaoImpl(
    private val transactionProvider: TransactionProvider = DomainTransactionProvider
) : AddressDao {
    override fun getAll(page: Int, pageSize: Int): List<Address> = transactionProvider.executeTransaction {
        Addresses.selectAll()
            .limit(pageSize)
            .offset(start = (page * pageSize).toLong())
            .map { row -> row.toAddress() }
    }

    override fun search(query: String, page: Int, pageSize: Int): List<Address> = transactionProvider.executeTransaction {
        val searchPattern = "%$query%"
        Addresses.selectAll()
            .where { 
                (Addresses.line like searchPattern) or
                (Addresses.suburb like searchPattern)
            }
            .limit(pageSize)
            .offset(start = (page * pageSize).toLong())
            .map { row -> row.toAddress() }
    }

    override fun getById(id: UUID): Address? = transactionProvider.executeTransaction {
        Addresses.selectAll()
            .where { Addresses.id eq id }
            .limit(1)
            .map { row -> row.toAddress() }
            .singleOrNull()
    }

    override fun create(address: Address): Unit = transactionProvider.executeTransaction {
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

    override fun update(address: Address): Unit = transactionProvider.executeTransaction {
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

    override fun delete(id: UUID): Unit = transactionProvider.executeTransaction {
        Addresses.deleteWhere { Addresses.id eq id }
        Unit
    }

    private fun ResultRow.toAddress(): Address {
        val entityId = this[Addresses.id] as EntityID<UUID>
        return Address(
            id = entityId.value,
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
