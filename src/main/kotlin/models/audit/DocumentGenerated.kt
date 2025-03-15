package models.audit

import java.time.LocalDateTime
import java.util.UUID

/**
 * Data class representing a generated document record.
 * This is the base class for all types of generated documents in the system.
 */
data class DocumentGenerated(
    val id: UUID,
    val documentType: String,
    val referenceNumber: String,
    val generatedAt: LocalDateTime,
    val generatedBy: UUID,
    val relatedEntityId: UUID,
    val relatedEntityType: String,
    val verificationCode: String? = null,
    val hash: String? = null,
    val filePath: String? = null
) {
    companion object {
        val default = DocumentGenerated(
            id = UUID.randomUUID(),
            documentType = "",
            referenceNumber = "",
            generatedAt = LocalDateTime.now(),
            generatedBy = UUID.randomUUID(),
            relatedEntityId = UUID.randomUUID(),
            relatedEntityType = "",
            verificationCode = null,
            hash = null,
            filePath = null
        )
    }
}