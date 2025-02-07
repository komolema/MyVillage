package models

import java.time.LocalDate
import java.util.*

data class Payment(
    val id: UUID,
    val date: LocalDate,
    val method: String,
    val note: String?,
    val price: Double
)