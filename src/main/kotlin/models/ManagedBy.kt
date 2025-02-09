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
) {
    companion object {
        val default = ManagedBy(
            id = UUID.randomUUID(),
            resourceId = UUID.randomUUID(),
            residentId = UUID.randomUUID(),
            status = "",
            appointmentDate = LocalDate.now(),
            position = ""
        )
    }
}