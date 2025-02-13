package database

import database.schema.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConfig(private val dbUrl: String, private val driver: String) {
    fun initialize() {
        // Connect to the database
        Database.connect(dbUrl, driver)

        // Create tables
        transaction {
            SchemaUtils.create(
                Residents,
                Addresses,
                Qualifications,
                Jobs,
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
    }

}