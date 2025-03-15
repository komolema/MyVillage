package database

import database.schema.domain.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Configuration for the domain database, which stores the core business data.
 */
object DatabaseConfig {
    private const val dbUrl = "jdbc:sqlite:village.db"
    private const val driver = "org.sqlite.JDBC"
    private var initialized = false

    fun initialize() {
        if (initialized) return

        // Connect to the domain database
        Database.connect(dbUrl, driver, "main")

        // Create tables
        transaction {
            SchemaUtils.create(
                Residents,
                Addresses,
                Qualifications,
                EmploymentTable,
                Animals,
                Dependants,
                Leadership,
                ManagedBy,
                Ownerships,
                Payments,
                Residences,
                Resources
                // Add other tables as needed
            )
        }

        initialized = true
    }
}
