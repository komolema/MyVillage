package database.schema.audit

import org.jetbrains.exposed.dao.id.UUIDTable

/**
 * Table for storing permission definitions.
 * Permissions define what actions can be performed.
 */
object Permissions : UUIDTable("Permissions") {
    val name = varchar("name", 100).uniqueIndex()
    val description = varchar("description", 500).nullable()
    val action = varchar("action", 100) // The action this permission allows (e.g., "view", "edit", "create", "delete")
}

/**
 * Table for mapping roles to permissions.
 */
object RolePermissions : UUIDTable("RolePermissions") {
    val roleId = uuid("roleId").references(Roles.id)
    val permissionId = uuid("permissionId").references(Permissions.id)
    
    init {
        uniqueIndex("role_permission_idx", roleId, permissionId) // Each role can have a permission only once
    }
}

/**
 * Table for mapping UI components to permissions.
 * This defines which permissions are required to access specific UI components.
 */
object ComponentPermissions : UUIDTable("ComponentPermissions") {
    val componentId = varchar("componentId", 200).uniqueIndex() // Unique identifier for the UI component
    val componentType = varchar("componentType", 100) // Type of component (e.g., "screen", "button", "tab")
    val permissionId = uuid("permissionId").references(Permissions.id)
    val description = varchar("description", 500).nullable() // Description of what this component does
}