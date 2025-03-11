package models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Data class representing a proof of address record.
 */
data class ProofOfAddress(
    val id: UUID,
    val residentId: UUID,
    val addressId: UUID,
    val referenceNumber: String,
    val generatedAt: LocalDateTime,
    val generatedBy: String,
    val verificationCode: String,
    val hash: String
) {
    companion object {
        val default = ProofOfAddress(
            id = UUID.randomUUID(),
            residentId = UUID.randomUUID(),
            addressId = UUID.randomUUID(),
            referenceNumber = "",
            generatedAt = LocalDateTime.now(),
            generatedBy = "",
            verificationCode = "",
            hash = ""
        )
    }
}