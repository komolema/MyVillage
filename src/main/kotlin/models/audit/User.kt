package models.audit

import java.time.LocalDateTime
import java.util.UUID

/**
 * Data class representing a user in the system.
 */
data class User(
    val id: UUID,
    val username: String,
    val passwordHash: String,
    val salt: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastLogin: LocalDateTime? = null,
    val residentId: UUID? = null
) {
    companion object {
        val default = User(
            id = UUID.randomUUID(),
            username = "",
            passwordHash = "",
            salt = "",
            firstName = "",
            lastName = "",
            email = "",
            phoneNumber = null,
            isActive = true,
            createdAt = LocalDateTime.now(),
            lastLogin = null,
            residentId = null
        )
    }
}