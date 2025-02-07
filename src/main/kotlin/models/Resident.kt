package models

import java.time.LocalDate
import java.util.*


data class Resident(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val dob: LocalDate,
    val gender: String,
    val idNumber: String,
    val phoneNumber: String?,
    val email: String?
)