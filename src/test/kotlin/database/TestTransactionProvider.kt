package database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * A test-specific implementation of TransactionProvider that uses the in-memory database.
 * This is used in tests to ensure that database operations are performed on the test database.
 */
class TestTransactionProvider(private val db: Database? = null) : TransactionProvider {
    override fun <T> executeTransaction(block: Transaction.() -> T): T {
        return if (db != null) {
            transaction(db) {
                block()
            }
        } else {
            transaction {
                block()
            }
        }
    }
}
