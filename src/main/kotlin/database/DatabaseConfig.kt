package database

import database.schema.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {
    private const val dbUrl = "jdbc:sqlite:village.db"
    private const val driver = "org.sqlite.JDBC"

    fun initialize() {
        // Connect to the database
        Database.connect(dbUrl, driver)

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
                Resources,
                ProofOfAddresses
                // Add other tables as needed
            )
        }
    }
}
