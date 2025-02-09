package models

import java.time.LocalDate
import java.util.*

data class Payment(
    val id: UUID,
    val date: LocalDate,
    val method: String,
    val note: String?,
    val price: Double
) {
    companion object {
        val default = Payment(
            id = UUID.randomUUID(),
            date = LocalDate.now(),
            method = "",
            note = null,
            price = 0.0
        )
    }
}