package database

import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * A test-specific implementation of TransactionProvider that uses the in-memory database.
 * This is used in tests to ensure that database operations are performed on the test database.
 */
class TestTransactionProvider : TransactionProvider {
    override fun <T> executeTransaction(block: Transaction.() -> T): T {
        return transaction {
            block()
        }
    }
}