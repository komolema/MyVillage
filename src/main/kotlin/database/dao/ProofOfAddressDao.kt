package database.dao

import database.schema.ProofOfAddresses
import models.ProofOfAddress
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime
import java.util.*

/**
 * Data Access Object interface for managing ProofOfAddress entities.
 * Provides CRUD operations and search functionality.
 */
interface ProofOfAddressDao {
    /**
     * Retrieves a paginated list of all proof of address records.
     *
     * @param page The page number (0-based)
     * @param pageSize The number of items per page
     * @return List of proof of address records for the specified page
     */
    fun getAll(page: Int, pageSize: Int): List<ProofOfAddress>

    /**
     * Retrieves all proof of address records for a specific resident.
     *
     * @param residentId The UUID of the resident
     * @return List of proof of address records for the resident
     */
    fun getByResidentId(residentId: UUID): List<ProofOfAddress>

    /**
     * Retrieves a proof of address record by its unique identifier.
     *
     * @param id The UUID of the proof of address record
     * @return The proof of address record if found, null otherwise
     */
    fun getById(id: UUID): ProofOfAddress?

    /**
     * Retrieves a proof of address record by its reference number.
     *
     * @param referenceNumber The reference number of the proof of address record
     * @return The proof of address record if found, null otherwise
     */
    fun getByReferenceNumber(referenceNumber: String): ProofOfAddress?

    /**
     * Creates a new proof of address record.
     *
     * @param proofOfAddress The proof of address record to create
     */
    fun create(proofOfAddress: ProofOfAddress)

    /**
     * Deletes a proof of address record by its unique identifier.
     *
     * @param id The UUID of the proof of address record to delete
     */
    fun delete(id: UUID)
}

class ProofOfAddressDaoImpl : ProofOfAddressDao {
    override fun getAll(page: Int, pageSize: Int): List<ProofOfAddress> = transaction {
        ProofOfAddresses.selectAll()
            .orderBy(ProofOfAddresses.generatedAt to SortOrder.DESC)
            .limit(pageSize)
            .offset(start = (page * pageSize).toLong())
            .map { row -> row.toProofOfAddress() }
    }

    override fun getByResidentId(residentId: UUID): List<ProofOfAddress> = transaction {
        ProofOfAddresses.selectAll()
            .where { ProofOfAddresses.residentId eq residentId }
            .orderBy(ProofOfAddresses.generatedAt to SortOrder.DESC)
            .map { row -> row.toProofOfAddress() }
    }

    override fun getById(id: UUID): ProofOfAddress? = transaction {
        ProofOfAddresses.selectAll()
            .where { ProofOfAddresses.id eq id }
            .limit(1)
            .map { row -> row.toProofOfAddress() }
            .singleOrNull()
    }

    override fun getByReferenceNumber(referenceNumber: String): ProofOfAddress? = transaction {
        ProofOfAddresses.selectAll()
            .where { ProofOfAddresses.referenceNumber eq referenceNumber }
            .limit(1)
            .map { row -> row.toProofOfAddress() }
            .singleOrNull()
    }

    override fun create(proofOfAddress: ProofOfAddress): Unit = transaction {
        ProofOfAddresses.insert {
            it[id] = proofOfAddress.id
            it[residentId] = proofOfAddress.residentId
            it[addressId] = proofOfAddress.addressId
            it[referenceNumber] = proofOfAddress.referenceNumber
            it[generatedAt] = proofOfAddress.generatedAt
            it[generatedBy] = proofOfAddress.generatedBy
            it[verificationCode] = proofOfAddress.verificationCode
            it[hash] = proofOfAddress.hash
        }
        Unit
    }

    override fun delete(id: UUID): Unit = transaction {
        ProofOfAddresses.deleteWhere { ProofOfAddresses.id eq id }
        Unit
    }

    private fun ResultRow.toProofOfAddress(): ProofOfAddress {
        val entityId = this[ProofOfAddresses.id] as EntityID<UUID>
        return ProofOfAddress(
            id = entityId.value,
            residentId = this[ProofOfAddresses.residentId],
            addressId = this[ProofOfAddresses.addressId],
            referenceNumber = this[ProofOfAddresses.referenceNumber],
            generatedAt = this[ProofOfAddresses.generatedAt],
            generatedBy = this[ProofOfAddresses.generatedBy],
            verificationCode = this[ProofOfAddresses.verificationCode],
            hash = this[ProofOfAddresses.hash]
        )
    }
}
