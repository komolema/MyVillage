package database

import database.schema.audit.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Configuration for the audit database, which stores:
 * 1. Document tracking information
 * 2. User authentication data
 * 3. Role and permission data
 */
object AuditDatabaseConfig {
    private const val dbUrl = "jdbc:sqlite:audit.db"
    private const val driver = "org.sqlite.JDBC"
    private var initialized = false

    fun initialize() {
        if (initialized) return
        
        // Connect to the audit database
        Database.connect(dbUrl, driver, "audit_db")

        // Create tables
        transaction {
            SchemaUtils.create(
                DocumentsGenerated,
                Users,
                Roles,
                UserRoles,
                Permissions,
                RolePermissions,
                ComponentPermissions
            )
        }
        
        initialized = true
    }
}