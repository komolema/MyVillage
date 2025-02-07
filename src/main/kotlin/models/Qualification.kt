package models

import java.time.LocalDate
import java.util.*

data class Qualification(
    val id: UUID,
    val residentId: UUID,
    val name: String,
    val instituteName: String,
    val nqfLevel: Int,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val city: String
)