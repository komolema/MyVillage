package database

import org.jetbrains.exposed.sql.Transaction

/**
 * Interface for providing database transactions.
 * This allows DAOs to be injected with the appropriate transaction function
 * for the database they should use (domain or audit).
 */
interface TransactionProvider {
    /**
     * Execute a block of code within a database transaction.
     *
     * @param block The code to execute within the transaction.
     * @return The result of the block execution.
     */
    fun <T> executeTransaction(block: Transaction.() -> T): T
}

/**
 * Implementation of TransactionProvider that uses the domain database.
 */
object DomainTransactionProvider : TransactionProvider {
    override fun <T> executeTransaction(block: Transaction.() -> T): T {
        return DatabaseManager.domainTransaction(block)
    }
}

/**
 * Implementation of TransactionProvider that uses the audit database.
 */
object AuditTransactionProvider : TransactionProvider {
    override fun <T> executeTransaction(block: Transaction.() -> T): T {
        return DatabaseManager.auditTransaction(block)
    }
}
