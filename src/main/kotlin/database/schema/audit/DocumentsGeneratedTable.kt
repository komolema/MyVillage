package database.schema.audit

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Table for tracking all generated documents in the system.
 * This includes proof of address documents and any other document types.
 */
object DocumentsGenerated : UUIDTable("DocumentsGenerated") {
    val documentType = varchar("documentType", 100) // Type of document (e.g., "ProofOfAddress", "Certificate", etc.)
    val referenceNumber = varchar("referenceNumber", 50).uniqueIndex()
    val generatedAt = datetime("generatedAt")
    val generatedBy = uuid("generatedBy") // Reference to Users table
    val relatedEntityId = uuid("relatedEntityId") // ID of the related entity (e.g., resident ID for proof of address)
    val relatedEntityType = varchar("relatedEntityType", 100) // Type of the related entity (e.g., "Resident")
    val verificationCode = varchar("verificationCode", 100).nullable() // For verification purposes
    val hash = varchar("hash", 256).nullable() // Hash of the document content for integrity verification
    val filePath = varchar("filePath", 500).nullable() // Path to the stored document file, if applicable
}