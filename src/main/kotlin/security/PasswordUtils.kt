package security

import org.mindrot.jbcrypt.BCrypt

/**
 * Utility class for password hashing and verification using BCrypt.
 */
object PasswordUtils {
    /**
     * Generates a salt for password hashing.
     * 
     * @return A salt string
     */
    fun generateSalt(): String {
        return BCrypt.gensalt()
    }
    
    /**
     * Hashes a password using BCrypt with the provided salt.
     * 
     * @param password The password to hash
     * @param salt The salt to use for hashing
     * @return The hashed password
     */
    fun hashPassword(password: String, salt: String): String {
        return BCrypt.hashpw(password, salt)
    }
    
    /**
     * Verifies a password against a hashed password.
     * 
     * @param password The password to verify
     * @param hashedPassword The hashed password to verify against
     * @return True if the password matches the hashed password, false otherwise
     */
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
    
    /**
     * Hashes a password using BCrypt with a newly generated salt.
     * 
     * @param password The password to hash
     * @return A pair of the hashed password and the salt used
     */
    fun hashPasswordWithNewSalt(password: String): Pair<String, String> {
        val salt = generateSalt()
        val hashedPassword = hashPassword(password, salt)
        return Pair(hashedPassword, salt)
    }
}