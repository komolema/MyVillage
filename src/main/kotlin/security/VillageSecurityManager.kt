package security

import database.DatabaseManager
import database.dao.audit.AuditDataBag
import database.dao.domain.DomainDataBag
import java.util.*

/**
 * Manages security and permissions for the application.
 */
class VillageSecurityManager(private val auditDataBag: AuditDataBag, private val domainDataBag: DomainDataBag) {
    private var currentUserId: UUID? = null
    private var currentUserRoles: List<String> = emptyList()

    /**
     * Initialize the security system.
     * This should be called during application startup.
     */
    fun initialize() {
        // Create default roles if they don't exist
        if (DatabaseManager.isFirstStartup()) {
            auditDataBag.userDao.createDefaultRoles()
        }
    }

    /**
     * Set the current user after successful authentication.
     * 
     * @param userId The ID of the authenticated user
     */
    fun setCurrentUser(userId: UUID) {
        currentUserId = userId
        // Load user roles from the database
        currentUserRoles = auditDataBag.userDao.getUserRoles(userId).map { it.name }
    }

    /**
     * Clear the current user (logout).
     */
    fun clearCurrentUser() {
        currentUserId = null
        currentUserRoles = emptyList()
    }

    /**
     * Check if a user is authenticated.
     * 
     * @return True if a user is currently authenticated
     */
    fun isAuthenticated(): Boolean {
        return currentUserId != null
    }

    /**
     * Check if the current user has a specific role.
     * 
     * @param role The role to check
     * @return True if the current user has the specified role
     */
    fun hasRole(role: String): Boolean {
        return currentUserRoles.contains(role)
    }

    /**
     * Check if the current user has permission to perform an action on a component.
     * 
     * @param componentId The ID of the component
     * @param action The action to check (e.g., "view", "edit", "create", "delete")
     * @return True if the current user has permission
     */
    fun hasPermission(componentId: String, action: String): Boolean {
        // In a real implementation, this would check the database
        // For now, we'll use a simple rule: ADMIN can do anything
        return hasRole("ADMIN") || hasRole("CHIEF")
    }

    /**
     * Create an admin user during first startup.
     * 
     * @param username The username for the admin user
     * @param password The password for the admin user
     * @param firstName The first name of the admin user
     * @param lastName The last name of the admin user
     * @param email The email address of the admin user
     * @param phoneNumber The phone number of the admin user (optional)
     * @return The ID of the newly created admin user
     */
    fun createAdminUser(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String? = null
    ): UUID {
        // Create the user
        val userId = auditDataBag.userDao.createUser(
            username = username,
            password = password,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber
        )

        // Assign the ADMIN role to the user
        val roles = auditDataBag.userDao.createDefaultRoles()
        val adminRoleId = roles["ADMIN"] ?: throw IllegalStateException("ADMIN role not found")
        auditDataBag.userDao.assignRoleToUser(userId, adminRoleId)

        // Also assign the CHIEF role to the user
        val chiefRoleId = roles["CHIEF"] ?: throw IllegalStateException("CHIEF role not found")
        auditDataBag.userDao.assignRoleToUser(userId, chiefRoleId)

        return userId
    }

    /**
     * Create a leadership record in the domain database for the admin user (chief).
     * 
     * @param firstName The first name of the chief
     * @param lastName The last name of the chief
     * @param villageName The name of the village
     */
    fun createChiefLeadership(firstName: String, lastName: String, villageName: String) {
        val fullName = "$firstName $lastName"
        val startDate = java.time.LocalDate.now()

        // Create a leadership model and use the leadership DAO
        val leadershipModel = models.domain.Leadership(
            id = UUID.randomUUID(),
            name = fullName,
            role = "Chief",
            startDate = startDate,
            villageName = villageName
        )

        domainDataBag.leadershipDao.createLeadership(leadershipModel)
    }
}
