package database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

/**
 * Manager for handling multiple databases in the application.
 * This class provides methods for initializing databases and executing transactions.
 */
object DatabaseManager {
    // Default database file paths
    private const val DOMAIN_DB_PATH = "jdbc:sqlite:village.db"
    private const val AUDIT_DB_PATH = "jdbc:sqlite:audit.db"

    // Current database paths (can be changed for testing)
    private var currentDomainDbPath = DOMAIN_DB_PATH
    private var currentAuditDbPath = AUDIT_DB_PATH

    // Flag to indicate if we're in test mode
    private var testMode = false

    /**
     * Initialize all databases used by the application.
     */
    fun initializeDatabases() {
        // Initialize domain database
        DatabaseConfig.initialize()

        // Initialize audit database
        AuditDatabaseConfig.initialize()
    }

    /**
     * Set up test mode with a temporary database file.
     * This should be called before any tests that use the database.
     */
    fun setupTestMode() {
        testMode = true

        // Create temporary database files
        val tempDomainDbFile = File.createTempFile("test_domain_", ".db")
        val tempAuditDbFile = File.createTempFile("test_audit_", ".db")

        // Delete the files when the JVM exits
        tempDomainDbFile.deleteOnExit()
        tempAuditDbFile.deleteOnExit()

        // Set the current database paths to the temporary files
        currentDomainDbPath = "jdbc:sqlite:${tempDomainDbFile.absolutePath}"
        currentAuditDbPath = "jdbc:sqlite:${tempAuditDbFile.absolutePath}"
    }

    /**
     * Reset test mode.
     * This should be called after tests to clean up.
     */
    fun resetTestMode() {
        testMode = false
        currentDomainDbPath = DOMAIN_DB_PATH
        currentAuditDbPath = AUDIT_DB_PATH
    }

    /**
     * Execute a transaction on the domain database.
     */
    fun <T> domainTransaction(block: Transaction.() -> T): T {
        return transaction(Database.connect(currentDomainDbPath, "org.sqlite.JDBC")) {
            block()
        }
    }

    /**
     * Execute a transaction on the audit database.
     */
    fun <T> auditTransaction(block: Transaction.() -> T): T {
        return transaction(Database.connect(currentAuditDbPath, "org.sqlite.JDBC")) {
            block()
        }
    }

    /**
     * Check if this is the first startup of the application.
     * This is determined by checking if the audit database has any users.
     */
    fun isFirstStartup(): Boolean {
        // For simplicity, we'll check if the audit.db file exists and has data
        val auditDbFile = java.io.File("audit.db")
        if (!auditDbFile.exists() || auditDbFile.length() < 100) {
            return true
        }

        // If the file exists and has some data, we'll assume it's not the first startup
        return false
    }
}
