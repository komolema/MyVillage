package models

import java.util.*

data class Dependent(
    val id: UUID,
    val residentId: UUID,
    val idNumber: String,
    val name: String,
    val surname: String,
    val gender: String
)