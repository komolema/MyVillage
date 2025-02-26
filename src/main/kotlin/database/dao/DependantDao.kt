package database.dao

import database.schema.Dependants
import models.Dependant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Data Access Object interface for managing Dependant entities.
 * Provides CRUD operations, search functionality, pagination support,
 * and resident-specific queries.
 */
interface DependantDao {
    /**
     * Retrieves a paginated list of all dependants.
     *
     * @param page The page number (0-based)
     * @param pageSize The number of items per page
     * @return List of dependants for the specified page
     */
    fun getAll(page: Int, pageSize: Int): List<Dependant>

    /**
     * Searches for dependants matching the given query string.
     * Searches across idNumber, name, and surname fields.
     *
     * @param query The search query string
     * @param page The page number (0-based)
     * @param pageSize The number of items per page
     * @return List of matching dependants
     */
    fun search(query: String, page: Int, pageSize: Int): List<Dependant>

    /**
     * Retrieves a dependant by its unique identifier.
     *
     * @param id The UUID of the dependant
     * @return The dependant if found, null otherwise
     */
    fun getById(id: UUID): Dependant?

    /**
     * Retrieves all dependants associated with a specific resident.
     * This is a key method for managing resident-dependant relationships.
     *
     * @param residentId The UUID of the resident
     * @return List of dependants associated with the resident
     */
    fun getDependentsByResidentId(residentId: UUID): List<Dependant>

    /**
     * Creates a new dependant record.
     *
     * @param dependant The dependant to create
     */
    fun create(dependant: Dependant): Unit

    /**
     * Updates an existing dependant record.
     *
     * @param dependant The dependant with updated information
     */
    fun update(dependant: Dependant): Unit

    /**
     * Deletes a dependant by its unique identifier.
     *
     * @param id The UUID of the dependant to delete
     */
    fun delete(id: UUID): Unit
}

class DependantDaoImpl : DependantDao {
    override fun getAll(page: Int, pageSize: Int): List<Dependant> = transaction {
        Dependants.selectAll()
            .limit(pageSize)
            .offset(start = (page * pageSize).toLong())
            .map { it.toDependant() }
    }

    override fun search(query: String, page: Int, pageSize: Int): List<Dependant> = transaction {
        Dependants.select(
            (Dependants.idNumber like "%$query%") or
            (Dependants.name like "%$query%") or
            (Dependants.surname like "%$query%")
        )
            .limit(pageSize)
            .offset(start = (page * pageSize).toLong())
            .map { it.toDependant() }
    }

    override fun getById(id: UUID): Dependant? = transaction {
        Dependants.select(Dependants.id eq id)
            .mapNotNull { it.toDependant() }
            .singleOrNull()
    }

    override fun getDependentsByResidentId(residentId: UUID): List<Dependant> = transaction {
        Dependants.select(Dependants.residentId eq residentId)
            .map { it.toDependant() }
    }

    override fun create(dependant: Dependant): Unit = transaction {
        Dependants.insert {
            it[id] = dependant.id
            it[residentId] = dependant.residentId
            it[idNumber] = dependant.idNumber
            it[name] = dependant.name
            it[surname] = dependant.surname
            it[gender] = dependant.gender
        }
        Unit
    }

    override fun update(dependant: Dependant): Unit = transaction {
        Dependants.update({ Dependants.id eq dependant.id }) {
            it[residentId] = dependant.residentId
            it[idNumber] = dependant.idNumber
            it[name] = dependant.name
            it[surname] = dependant.surname
            it[gender] = dependant.gender
        }
        Unit
    }

    override fun delete(id: UUID): Unit = transaction {
        Dependants.deleteWhere { Dependants.id eq id }
        Unit
    }

    private fun ResultRow.toDependant(): Dependant {
        return Dependant(
            id = this[Dependants.id].value,
            residentId = this[Dependants.residentId],
            idNumber = this[Dependants.idNumber],
            name = this[Dependants.name],
            surname = this[Dependants.surname],
            gender = this[Dependants.gender]
        )
    }
}
