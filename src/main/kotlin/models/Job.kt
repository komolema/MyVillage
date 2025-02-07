package models

import java.time.LocalDate
import java.util.*

data class Job(
    val id: UUID,
    val residentId: UUID,
    val employer: String,
    val role: String,
    val startDate: LocalDate,
    val endDate: LocalDate?
)