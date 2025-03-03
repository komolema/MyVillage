package models

import java.util.*

data class Dependant(
    val id: UUID,
    val residentId: UUID,
    val idNumber: String,
    val name: String,
    val surname: String,
    val gender: String
) {
    init {
        require(gender.isEmpty() || gender in VALID_GENDERS) {
            "Invalid gender value. Must be one of: ${VALID_GENDERS.joinToString()}"
        }
    }

    companion object {
        val VALID_GENDERS = setOf("Male", "Female", "Other")

        val default = Dependant(
            id = UUID.randomUUID(),
            residentId = UUID.randomUUID(),
            idNumber = "",
            name = "",
            surname = "",
            gender = ""
        )
    }
}
