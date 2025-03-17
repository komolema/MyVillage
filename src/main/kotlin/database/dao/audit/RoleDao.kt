package database.dao.audit

import database.AuditTransactionProvider
import database.DatabaseManager
import database.TransactionProvider
import database.schema.audit.Roles
import database.schema.audit.UserRoles
import models.audit.Role
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * Interface for the Role Data Access Object.
 */
interface RoleDao {
    fun create(role: Role): Role
    fun getById(id: UUID): Role?
    fun getByName(name: String): Role?
    fun getAll(): List<Role>
    fun update(role: Role): Role
    fun delete(id: UUID): Boolean
    fun getUsersWithRole(roleId: UUID): List<UUID>
}

/**
 * Implementation of the RoleDao interface.
 * Provides CRUD operations for roles.
 */
class RoleDaoImpl(
    private val transactionProvider: TransactionProvider = AuditTransactionProvider
) : RoleDao {
    /**
     * Creates a new role in the database.
     *
     * @param role The role to create
     * @return The created role
     */
    override fun create(role: Role): Role = transactionProvider.executeTransaction {
        Roles.insert {
            it[id] = role.id
            it[name] = role.name
            it[description] = role.description
            it[isSystem] = role.isSystem
        }
        role
    }

    /**
     * Retrieves a role by its ID.
     *
     * @param id The ID of the role to retrieve
     * @return The role if found, null otherwise
     */
    override fun getById(id: UUID): Role? = transactionProvider.executeTransaction {
        Roles.selectAll()
            .where { Roles.id eq id }
            .limit(1)
            .map { toRole(it) }
            .singleOrNull()
    }

    /**
     * Retrieves a role by its name.
     *
     * @param name The name of the role to retrieve
     * @return The role if found, null otherwise
     */
    override fun getByName(name: String): Role? = transactionProvider.executeTransaction {
        Roles.selectAll()
            .where { Roles.name eq name }
            .limit(1)
            .map { toRole(it) }
            .singleOrNull()
    }

    /**
     * Retrieves all roles.
     *
     * @return A list of all roles
     */
    override fun getAll(): List<Role> = transactionProvider.executeTransaction {
        Roles.selectAll()
            .map { toRole(it) }
    }

    /**
     * Updates an existing role in the database.
     *
     * @param role The role to update
     * @return The updated role
     */
    override fun update(role: Role): Role = transactionProvider.executeTransaction {
        Roles.update({ Roles.id eq role.id }) {
            it[name] = role.name
            it[description] = role.description
            it[isSystem] = role.isSystem
        }
        role
    }

    /**
     * Deletes a role from the database.
     *
     * @param id The ID of the role to delete
     * @return true if the role was deleted, false otherwise
     */
    override fun delete(id: UUID): Boolean = transactionProvider.executeTransaction {
        // Check if it's a system role
        val role = getById(id)
        if (role?.isSystem == true) {
            return@executeTransaction false
        }

        // First delete any user role mappings
        UserRoles.deleteWhere { UserRoles.roleId eq id }

        // Then delete the role
        Roles.deleteWhere { Roles.id eq id } > 0
    }

    /**
     * Retrieves all users with a specific role.
     *
     * @param roleId The ID of the role
     * @return A list of user IDs with the specified role
     */
    override fun getUsersWithRole(roleId: UUID): List<UUID> = transactionProvider.executeTransaction {
        UserRoles.selectAll()
            .where { UserRoles.roleId eq roleId }
            .map { (it[UserRoles.userId] as EntityID<UUID>).value }
    }

    /**
     * Converts a ResultRow to a Role object.
     */
    private fun toRole(row: ResultRow): Role {
        val entityId = row[Roles.id] as EntityID<UUID>
        return Role(
            id = entityId.value,
            name = row[Roles.name],
            description = row[Roles.description],
            isSystem = row[Roles.isSystem]
        )
    }
}
