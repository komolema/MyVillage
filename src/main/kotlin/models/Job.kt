package models

data class Job(
    val id: java.util.UUID,
    val residentId: java.util.UUID,
    val employer: String,
    val role: String,
    val startDate: java.time.LocalDate,
    val endDate: java.time.LocalDate? = null
)
