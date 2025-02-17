package models

import java.time.LocalDate
import java.util.*

data class Qualification(
    val id: UUID,
    val residentId: UUID,
    val name: String,
    val institution: String,
    val nqfLevel: Int,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val city: String
) {
    companion object {
        val default = Qualification(
            id = UUID.randomUUID(),
            residentId = UUID.randomUUID(),
            name = "",
            institution = "",
            nqfLevel = 0,
            startDate = LocalDate.now(),
            endDate = null,
            city = ""
        )
    }
}