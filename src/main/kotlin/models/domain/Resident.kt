package models.domain

import java.time.LocalDate
import java.util.UUID

data class Resident(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val dob: LocalDate,
    val gender: String,
    val idNumber: String,
    val phoneNumber: String? = null,
    val email: String? = null
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
