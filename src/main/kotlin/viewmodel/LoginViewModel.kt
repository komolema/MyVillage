package viewmodel

import database.dao.audit.UserDao
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import security.PasswordUtils
import security.VillageSecurityManager

/**
 * ViewModel for the login screen.
 * Adapted to use JavaFX properties instead of Compose state.
 */
class LoginViewModel(
    private val userDao: UserDao,
    private val securityManager: VillageSecurityManager
) {
    // JavaFX properties
    val usernameProperty = SimpleStringProperty("")
    val passwordProperty = SimpleStringProperty("")
    val isLoadingProperty = SimpleBooleanProperty(false)
    val errorMessageProperty = SimpleStringProperty("")

    // Convenience getters
    val username: String get() = usernameProperty.get()
    val password: String get() = passwordProperty.get()
    val isLoading: Boolean get() = isLoadingProperty.get()
    val errorMessage: String get() = errorMessageProperty.get()

    /**
     * Updates the username.
     * 
     * @param newUsername The new username
     */
    fun updateUsername(newUsername: String) {
        usernameProperty.set(newUsername)
        errorMessageProperty.set("")
    }

    /**
     * Updates the password.
     * 
     * @param newPassword The new password
     */
    fun updatePassword(newPassword: String) {
        passwordProperty.set(newPassword)
        errorMessageProperty.set("")
    }

    /**
     * Attempts to log in with the current username and password.
     * 
     * @return True if login was successful, false otherwise
     */
    fun login(): Boolean {
        if (username.isBlank() || password.isBlank()) {
            errorMessageProperty.set("Username and password cannot be empty")
            return false
        }

        isLoadingProperty.set(true)
        errorMessageProperty.set("")

        try {
            // Get the user by username
            val user = userDao.getByUsername(username)

            if (user == null) {
                errorMessageProperty.set("Invalid username or password")
                isLoadingProperty.set(false)
                return false
            }

            // Verify the password
            val isPasswordValid = PasswordUtils.verifyPassword(password, user.passwordHash)

            if (!isPasswordValid) {
                errorMessageProperty.set("Invalid username or password")
                isLoadingProperty.set(false)
                return false
            }

            // Set the current user in the security manager
            securityManager.setCurrentUser(user.id)

            // Update the last login time
            userDao.updateLastLogin(user.id)

            isLoadingProperty.set(false)
            return true
        } catch (e: Exception) {
            errorMessageProperty.set("An error occurred: ${e.message}")
            isLoadingProperty.set(false)
            return false
        }
    }

    /**
     * Clears the login form.
     */
    fun clearForm() {
        usernameProperty.set("")
        passwordProperty.set("")
        errorMessageProperty.set("")
    }
}
