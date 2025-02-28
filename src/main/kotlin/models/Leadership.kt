package models

import java.time.LocalDate
import java.util.UUID

data class Leadership(
    val id: UUID,
    val name: String,
    val role: String,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val villageName: String
)