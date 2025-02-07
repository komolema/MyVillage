package models

import java.time.LocalDate
import java.util.*


data class ManagedBy(
    val id: UUID,
    val resourceId: UUID,
    val residentId: UUID,
    val status: String,
    val appointmentDate: LocalDate,
    val position: String
)