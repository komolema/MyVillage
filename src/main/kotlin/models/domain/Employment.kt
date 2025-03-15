package models.domain

import java.time.LocalDate
import java.util.*

data class Employment(
    val id: UUID,
    val residentId: UUID,
    val employer: String,
    val role: String,
    val startDate: LocalDate,
    val endDate: LocalDate?
) {
    companion object {
        val default = Employment(
            id = UUID.randomUUID(),
            residentId = UUID.randomUUID(),
            employer = "",
            role = "",
            startDate = LocalDate.now(),
            endDate = null
        )
    }
}