package models.audit

import models.domain.Address
import models.domain.Resident
import java.time.LocalDateTime
import java.util.UUID

/**
 * Data class representing a proof of address document.
 * This uses composition with DocumentGenerated to represent a proof of address document.
 */
data class ProofOfAddressDocument(
    val document: DocumentGenerated,
    val addressId: UUID
) {
    val id: UUID get() = document.id
    val referenceNumber: String get() = document.referenceNumber
    val generatedAt: LocalDateTime get() = document.generatedAt
    val generatedBy: UUID get() = document.generatedBy
    val residentId: UUID get() = document.relatedEntityId
    val verificationCode: String? get() = document.verificationCode
    val hash: String? get() = document.hash
    val filePath: String? get() = document.filePath

    companion object {
        fun create(
            resident: Resident,
            address: Address,
            referenceNumber: String,
            verificationCode: String,
            hash: String,
            generatedBy: UUID,
            filePath: String? = null
        ): ProofOfAddressDocument {
            val document = DocumentGenerated(
                id = UUID.randomUUID(),
                documentType = "ProofOfAddress",
                referenceNumber = referenceNumber,
                generatedAt = LocalDateTime.now(),
                generatedBy = generatedBy,
                relatedEntityId = resident.id,
                relatedEntityType = "Resident",
                verificationCode = verificationCode,
                hash = hash,
                filePath = filePath
            )
            
            return ProofOfAddressDocument(
                document = document,
                addressId = address.id
            )
        }
    }
}