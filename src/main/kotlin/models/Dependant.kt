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
    companion object {
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