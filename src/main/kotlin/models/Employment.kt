package models

import java.time.LocalDate
import java.util.*

data class Employment(
    val id: UUID,
    val residentId: UUID,
    val employer: String,
    val role: String,
    val startDate: LocalDate,
    val endDate: LocalDate?
)