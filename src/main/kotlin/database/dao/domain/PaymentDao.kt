package database.dao.domain

import database.DomainTransactionProvider
import database.TransactionProvider
import database.schema.domain.Payments
import models.domain.Payment
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

interface PaymentDao {
    fun createPayment(payment: Payment): Payment
    fun getPaymentById(id: UUID): Payment?
    fun getAllPayments(): List<Payment>
    fun getPaymentsByDate(date: LocalDate): List<Payment>
    fun getPaymentsByMethod(method: String): List<Payment>
    fun updatePayment(payment: Payment): Boolean
    fun deletePayment(id: UUID): Boolean
}

class PaymentDaoImpl(
    private val transactionProvider: TransactionProvider = DomainTransactionProvider
) : PaymentDao {
    override fun createPayment(payment: Payment): Payment = transactionProvider.executeTransaction {
        val id = Payments.insertAndGetId {
            it[date] = payment.date
            it[method] = payment.method
            it[note] = payment.note
            it[price] = BigDecimal.valueOf(payment.price)
        }
        payment.copy(id = id.value)
    }

    override fun getPaymentById(id: UUID): Payment? = transactionProvider.executeTransaction {
        Payments.select(Payments.id eq id)
            .map { it.toPayment() }
            .singleOrNull()
    }

    override fun getAllPayments(): List<Payment> = transactionProvider.executeTransaction {
        Payments.selectAll()
            .map { it.toPayment() }
    }

    override fun getPaymentsByDate(date: LocalDate): List<Payment> = transactionProvider.executeTransaction {
        Payments.select(Payments.date eq date)
            .map { it.toPayment() }
    }


    override fun getPaymentsByMethod(method: String): List<Payment> = transactionProvider.executeTransaction {
        Payments.select(Payments.method eq method)
            .map { it.toPayment() }
    }

    override fun updatePayment(payment: Payment): Boolean = transactionProvider.executeTransaction {
        Payments.update({ Payments.id eq payment.id }) {
            it[date] = payment.date
            it[method] = payment.method
            it[note] = payment.note
            it[price] = BigDecimal.valueOf(payment.price)
        } > 0
    }

    override fun deletePayment(id: UUID): Boolean = transactionProvider.executeTransaction {
        Payments.deleteWhere { Payments.id eq id } > 0
    }

    private fun ResultRow.toPayment(): Payment {
        return Payment(
            id = this[Payments.id].value,
            date = this[Payments.date],
            method = this[Payments.method],
            note = this[Payments.note],
            price = this[Payments.price].toDouble()
        )
    }
}
