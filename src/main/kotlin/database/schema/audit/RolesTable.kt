package database.schema.audit

import org.jetbrains.exposed.dao.id.UUIDTable

/**
 * Table for storing role definitions.
 */
object Roles : UUIDTable("Roles") {
    val name = varchar("name", 100).uniqueIndex()
    val description = varchar("description", 500).nullable()
    val isSystem = bool("isSystem").default(false) // System roles cannot be deleted
}

/**
 * Table for mapping users to roles.
 */
object UserRoles : UUIDTable("UserRoles") {
    val userId = uuid("userId").references(Users.id)
    val roleId = uuid("roleId").references(Roles.id)
    
    init {
        uniqueIndex("user_role_idx", userId, roleId) // Each user can have a role only once
    }
}