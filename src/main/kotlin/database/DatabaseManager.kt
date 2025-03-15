package database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Manager for handling multiple databases in the application.
 * This class provides methods for initializing databases and executing transactions.
 */
object DatabaseManager {

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
     * Execute a transaction on the domain database.
     */
    fun <T> domainTransaction(block: Transaction.() -> T): T {
        return transaction(Database.connect("jdbc:sqlite:village.db", "org.sqlite.JDBC", "domain_db")) {
            block()
        }
    }

    /**
     * Execute a transaction on the audit database.
     */
    fun <T> auditTransaction(block: Transaction.() -> T): T {
        return transaction(Database.connect("jdbc:sqlite:audit.db", "org.sqlite.JDBC", "audit_db")) {
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
