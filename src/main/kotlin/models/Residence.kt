package models

import java.time.LocalDate
import java.util.*

data class Residence(
    val id: UUID,
    val residentId: UUID,
    val addressId: UUID,
    val occupationDate: LocalDate
) {
    companion object {
        val default = Residence(
            id = UUID.randomUUID(),
            residentId = UUID.randomUUID(),
            addressId = UUID.randomUUID(),
            occupationDate = LocalDate.now()
        )
    }
}