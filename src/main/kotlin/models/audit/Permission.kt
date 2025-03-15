package models.audit

import java.util.UUID

/**
 * Data class representing a permission in the system.
 */
data class Permission(
    val id: UUID,
    val name: String,
    val description: String? = null,
    val action: String
) {
    companion object {
        val default = Permission(
            id = UUID.randomUUID(),
            name = "",
            description = null,
            action = ""
        )
    }
}

/**
 * Data class representing a mapping between a role and a permission.
 */
data class RolePermission(
    val id: UUID,
    val roleId: UUID,
    val permissionId: UUID
) {
    companion object {
        val default = RolePermission(
            id = UUID.randomUUID(),
            roleId = UUID.randomUUID(),
            permissionId = UUID.randomUUID()
        )
    }
}

/**
 * Data class representing a mapping between a UI component and a permission.
 */
data class ComponentPermission(
    val id: UUID,
    val componentId: String,
    val componentType: String,
    val permissionId: UUID,
    val description: String? = null
) {
    companion object {
        val default = ComponentPermission(
            id = UUID.randomUUID(),
            componentId = "",
            componentType = "",
            permissionId = UUID.randomUUID(),
            description = null
        )
    }
}