package database.dao

import database.schema.Resources
import models.Resource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Data Access Object interface for managing Resource entities.
 * Provides CRUD operations, search functionality, and pagination support.
 */
interface ResourceDao {
    /**
     * Retrieves a paginated list of all resources.
     *
     * @param page The page number (0-based)
     * @param pageSize The number of items per page
     * @return List of resources for the specified page
     */
    fun getAll(page: Int, pageSize: Int): List<Resource>

    /**
     * Searches for resources matching the given query string.
     * Searches across type and location fields.
     *
     * @param query The search query string
     * @param page The page number (0-based)
     * @param pageSize The number of items per page
     * @return List of matching resources
     */
    fun search(query: String, page: Int, pageSize: Int): List<Resource>

    /**
     * Retrieves a resource by its unique identifier.
     *
     * @param id The UUID of the resource
     * @return The resource if found, null otherwise
     */
    fun getById(id: UUID): Resource?

    /**
     * Creates a new resource record.
     *
     * @param resource The resource to create
     */
    fun create(resource: Resource): Unit

    /**
     * Updates an existing resource record.
     *
     * @param resource The resource with updated information
     */
    fun update(resource: Resource): Unit

    /**
     * Deletes a resource by its unique identifier.
     *
     * @param id The UUID of the resource to delete
     */
    fun delete(id: UUID): Unit
}

class ResourceDaoImpl : ResourceDao {
    override fun getAll(page: Int, pageSize: Int): List<Resource> = transaction {
        Resources.selectAll()
            .limit(pageSize)
            .offset(start = (page * pageSize).toLong())
            .map { it.toResource() }
    }

    override fun search(query: String, page: Int, pageSize: Int): List<Resource> = transaction {
        Resources.select(
            (Resources.type like "%$query%") or
            (Resources.location like "%$query%")
        )
            .limit(pageSize)
            .offset(start = (page * pageSize).toLong())
            .map { it.toResource() }
    }

    override fun getById(id: UUID): Resource? = transaction {
        Resources.select(Resources.id eq id)
            .mapNotNull { it.toResource() }
            .singleOrNull()
    }

    override fun create(resource: Resource): Unit = transaction {
        Resources.insert {
            it[id] = resource.id
            it[type] = resource.type
            it[location] = resource.location
        }
        Unit
    }

    override fun update(resource: Resource): Unit = transaction {
        Resources.update({ Resources.id eq resource.id }) {
            it[type] = resource.type
            it[location] = resource.location
        }
        Unit
    }

    override fun delete(id: UUID): Unit = transaction {
        Resources.deleteWhere { Resources.id eq id }
        Unit
    }

    private fun ResultRow.toResource(): Resource {
        return Resource(
            id = this[Resources.id].value,
            type = this[Resources.type],
            location = this[Resources.location]
        )
    }
}
