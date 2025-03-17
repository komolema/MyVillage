package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import database.dao.audit.UserDao
import security.PasswordUtils
import security.VillageSecurityManager
import java.util.UUID

/**
 * ViewModel for the login screen.
 */
class LoginViewModel(
    private val userDao: UserDao,
    private val securityManager: VillageSecurityManager
) {
    var username by mutableStateOf("")
        private set
    
    var password by mutableStateOf("")
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    /**
     * Updates the username.
     * 
     * @param newUsername The new username
     */
    fun updateUsername(newUsername: String) {
        username = newUsername
        errorMessage = null
    }
    
    /**
     * Updates the password.
     * 
     * @param newPassword The new password
     */
    fun updatePassword(newPassword: String) {
        password = newPassword
        errorMessage = null
    }
    
    /**
     * Attempts to log in with the current username and password.
     * 
     * @return True if login was successful, false otherwise
     */
    fun login(): Boolean {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Username and password cannot be empty"
            return false
        }
        
        isLoading = true
        errorMessage = null
        
        try {
            // Get the user by username
            val user = userDao.getByUsername(username)
            
            if (user == null) {
                errorMessage = "Invalid username or password"
                isLoading = false
                return false
            }
            
            // Verify the password
            val isPasswordValid = PasswordUtils.verifyPassword(password, user.passwordHash)
            
            if (!isPasswordValid) {
                errorMessage = "Invalid username or password"
                isLoading = false
                return false
            }
            
            // Set the current user in the security manager
            securityManager.setCurrentUser(user.id)
            
            // Update the last login time
            userDao.updateLastLogin(user.id)
            
            isLoading = false
            return true
        } catch (e: Exception) {
            errorMessage = "An error occurred: ${e.message}"
            isLoading = false
            return false
        }
    }
    
    /**
     * Clears the login form.
     */
    fun clearForm() {
        username = ""
        password = ""
        errorMessage = null
    }
}