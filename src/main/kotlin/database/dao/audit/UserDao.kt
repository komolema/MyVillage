package database.dao.audit

import database.DatabaseManager
import database.schema.audit.Users
import database.schema.audit.Roles
import database.schema.audit.UserRoles
import models.audit.User
import models.audit.Role
import models.audit.UserRole
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime
import java.util.UUID

/**
 * Interface for the User Data Access Object.
 */
interface UserDao {
    fun createUser(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String? = null
    ): UUID

    fun getById(id: UUID): User?
    fun getByUsername(username: String): User?
    fun getByEmail(email: String): User?
    fun update(user: User): User
    fun delete(id: UUID): Boolean
    fun createDefaultRoles(): Map<String, UUID>
    fun assignRoleToUser(userId: UUID, roleId: UUID): UserRole
    fun getUserRoles(userId: UUID): List<Role>
}

/**
 * Implementation of the UserDao interface.
 * Provides CRUD operations for users and user roles.
 */
class UserDaoImpl : UserDao {
    /**
     * Creates a new user in the database.
     *
     * @param username The username for the new user
     * @param password The password for the new user (will be hashed)
     * @param firstName The first name of the new user
     * @param lastName The last name of the new user
     * @param email The email address of the new user
     * @param phoneNumber The phone number of the new user (optional)
     * @return The ID of the newly created user
     */
    override fun createUser(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String?
    ): UUID = DatabaseManager.auditTransaction {
        // In a real implementation, we would hash the password
        val passwordHash = password // Placeholder for password hashing
        val salt = "salt" // Placeholder for salt generation

        val userId = Users.insert {
            it[id] = UUID.randomUUID()
            it[Users.username] = username
            it[Users.passwordHash] = passwordHash
            it[Users.salt] = salt
            it[Users.firstName] = firstName
            it[Users.lastName] = lastName
            it[Users.email] = email
            it[Users.phoneNumber] = phoneNumber
            it[isActive] = true
            it[createdAt] = LocalDateTime.now()
            it[lastLogin] = null
            it[residentId] = null
        } get Users.id

        return@auditTransaction userId.value
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve
     * @return The user if found, null otherwise
     */
    override fun getById(id: UUID): User? = DatabaseManager.auditTransaction {
        Users.selectAll()
            .where { Users.id eq id }
            .limit(1)
            .map { toUser(it) }
            .singleOrNull()
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve
     * @return The user if found, null otherwise
     */
    override fun getByUsername(username: String): User? = DatabaseManager.auditTransaction {
        Users.selectAll()
            .where { Users.username eq username }
            .limit(1)
            .map { toUser(it) }
            .singleOrNull()
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address of the user to retrieve
     * @return The user if found, null otherwise
     */
    override fun getByEmail(email: String): User? = DatabaseManager.auditTransaction {
        Users.selectAll()
            .where { Users.email eq email }
            .limit(1)
            .map { toUser(it) }
            .singleOrNull()
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user The user to update
     * @return The updated user
     */
    override fun update(user: User): User = DatabaseManager.auditTransaction {
        Users.update({ Users.id eq user.id }) {
            it[username] = user.username
            it[passwordHash] = user.passwordHash
            it[salt] = user.salt
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[phoneNumber] = user.phoneNumber
            it[isActive] = user.isActive
            it[lastLogin] = user.lastLogin
            it[residentId] = user.residentId
        }
        user
    }

    /**
     * Deletes a user from the database.
     *
     * @param id The ID of the user to delete
     * @return true if the user was deleted, false otherwise
     */
    override fun delete(id: UUID): Boolean = DatabaseManager.auditTransaction {
        // First delete any user roles
        UserRoles.deleteWhere { UserRoles.userId eq id }

        // Then delete the user
        Users.deleteWhere { Users.id eq id } > 0
    }

    /**
     * Creates default roles in the database if they don't already exist.
     *
     * @return A map of role names to role IDs
     */
    override fun createDefaultRoles(): Map<String, UUID> = DatabaseManager.auditTransaction {
        val roles = mutableMapOf<String, UUID>()

        // Define default roles
        val defaultRoles = listOf(
            Role(UUID.randomUUID(), "ADMIN", "Administrator with full access", true),
            Role(UUID.randomUUID(), "CHIEF", "Village chief with leadership access", true),
            Role(UUID.randomUUID(), "RESIDENT", "Regular village resident", true),
            Role(UUID.randomUUID(), "GUEST", "Guest with limited access", true)
        )

        // Create each role if it doesn't exist
        defaultRoles.forEach { role ->
            val existingRole = Roles.selectAll()
                .where { Roles.name eq role.name }
                .limit(1)
                .map { toRole(it) }
                .singleOrNull()

            val roleId = if (existingRole != null) {
                existingRole.id
            } else {
                val id = Roles.insert {
                    it[id] = role.id
                    it[name] = role.name
                    it[description] = role.description
                    it[isSystem] = role.isSystem
                } get Roles.id
                id.value
            }

            roles[role.name] = roleId
        }

        roles
    }

    /**
     * Assigns a role to a user.
     *
     * @param userId The ID of the user
     * @param roleId The ID of the role to assign
     * @return The created user role mapping
     */
    override fun assignRoleToUser(userId: UUID, roleId: UUID): UserRole = DatabaseManager.auditTransaction {
        // Check if the user already has this role
        val existingUserRole = UserRoles.selectAll()
            .where { (UserRoles.userId eq userId) and (UserRoles.roleId eq roleId) }
            .limit(1)
            .map { toUserRole(it) }
            .singleOrNull()

        if (existingUserRole != null) {
            return@auditTransaction existingUserRole
        }

        // Create the user role mapping
        val userRoleId = UserRoles.insert {
            it[id] = UUID.randomUUID()
            it[UserRoles.userId] = userId
            it[UserRoles.roleId] = roleId
        } get UserRoles.id

        UserRole(userRoleId.value, userId, roleId)
    }

    /**
     * Retrieves all roles assigned to a user.
     *
     * @param userId The ID of the user
     * @return A list of roles assigned to the user
     */
    override fun getUserRoles(userId: UUID): List<Role> = DatabaseManager.auditTransaction {
        (UserRoles innerJoin Roles)
            .selectAll()
            .where { UserRoles.userId eq userId }
            .map { toRole(it) }
    }

    /**
     * Converts a ResultRow to a User object.
     */
    private fun toUser(row: ResultRow): User {
        val entityId = row[Users.id] as EntityID<UUID>
        return User(
            id = entityId.value,
            username = row[Users.username],
            passwordHash = row[Users.passwordHash],
            salt = row[Users.salt],
            firstName = row[Users.firstName],
            lastName = row[Users.lastName],
            email = row[Users.email],
            phoneNumber = row[Users.phoneNumber],
            isActive = row[Users.isActive],
            createdAt = row[Users.createdAt],
            lastLogin = row[Users.lastLogin],
            residentId = row[Users.residentId]
        )
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

    /**
     * Converts a ResultRow to a UserRole object.
     */
    private fun toUserRole(row: ResultRow): UserRole {
        val entityId = row[UserRoles.id] as EntityID<UUID>
        return UserRole(
            id = entityId.value,
            userId = (row[UserRoles.userId] as EntityID<UUID>).value,
            roleId = (row[UserRoles.roleId] as EntityID<UUID>).value
        )
    }
}
