package models.domain

import java.time.LocalDate
import java.util.*

data class Ownership(
    val id: UUID,
    val residentId: UUID,
    val animalId: UUID,
    val paymentId: UUID?,
    val valid: Boolean,
    val acquisitionDate: LocalDate,
    val acquisitionMethod: String,
    val ownershipType: String,
    val sharedWith: String?
) {
    companion object {
        val default = Ownership(
            id = UUID.randomUUID(),
            residentId = UUID.randomUUID(),
            animalId = UUID.randomUUID(),
            paymentId = null,
            valid = false,
            acquisitionDate = LocalDate.now(),
            acquisitionMethod = "",
            ownershipType = "",
            sharedWith = null
        )
    }
}