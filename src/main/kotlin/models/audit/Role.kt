package models.audit

import java.util.UUID

/**
 * Data class representing a role in the system.
 */
data class Role(
    val id: UUID,
    val name: String,
    val description: String? = null,
    val isSystem: Boolean = false
) {
    companion object {
        val default = Role(
            id = UUID.randomUUID(),
            name = "",
            description = null,
            isSystem = false
        )
    }
}

/**
 * Data class representing a mapping between a user and a role.
 */
data class UserRole(
    val id: UUID,
    val userId: UUID,
    val roleId: UUID
) {
    companion object {
        val default = UserRole(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            roleId = UUID.randomUUID()
        )
    }
}