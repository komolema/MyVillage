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
) {
    companion object {
        val default = Resident(
            id = UUID.randomUUID(),
            firstName = "",
            lastName = "",
            dob = LocalDate.now(),
            gender = "",
            idNumber = "",
            phoneNumber = null,
            email = null
        )
    }
}