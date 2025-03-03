package database.dao

import database.schema.Payments
import models.Payment
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
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

class PaymentDaoImpl : PaymentDao {
    override fun createPayment(payment: Payment): Payment = transaction {
        val id = Payments.insertAndGetId {
            it[date] = payment.date
            it[method] = payment.method
            it[note] = payment.note
            it[price] = BigDecimal.valueOf(payment.price)
        }
        payment.copy(id = id.value)
    }

    override fun getPaymentById(id: UUID): Payment? = transaction {
        Payments.select(Payments.id eq id)
            .map { it.toPayment() }
            .singleOrNull()
    }

    override fun getAllPayments(): List<Payment> = transaction {
        Payments.selectAll()
            .map { it.toPayment() }
    }

    override fun getPaymentsByDate(date: LocalDate): List<Payment> = transaction {
        Payments.select(Payments.date eq date)
            .map { it.toPayment() }
    }


    override fun getPaymentsByMethod(method: String): List<Payment> = transaction {
        Payments.select(Payments.method eq method)
            .map { it.toPayment() }
    }

    override fun updatePayment(payment: Payment): Boolean = transaction {
        Payments.update({ Payments.id eq payment.id }) {
            it[date] = payment.date
            it[method] = payment.method
            it[note] = payment.note
            it[price] = BigDecimal.valueOf(payment.price)
        } > 0
    }

    override fun deletePayment(id: UUID): Boolean = transaction {
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
