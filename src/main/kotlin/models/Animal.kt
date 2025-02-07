package models

import java.time.LocalDate
import java.util.*

data class Animal(
    val id: UUID,
    val species: String,
    val breed: String,
    val gender: String,
    val dob: LocalDate,
    val tagNumber: String,
    val healthStatus: String,
    val vaccinationStatus: Boolean,
    val vaccinationDate: LocalDate?
)