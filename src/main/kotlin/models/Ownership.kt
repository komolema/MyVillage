package models

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
)