package database.schema

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

// Proof of Address Table
object ProofOfAddresses : UUIDTable("ProofOfAddresses") {
    val residentId = uuid("residentId").references(Residents.id)
    val addressId = uuid("addressId").references(Addresses.id)
    val referenceNumber = varchar("referenceNumber", 50).uniqueIndex()
    val generatedAt = datetime("generatedAt")
    val generatedBy = varchar("generatedBy", 100) // User who generated the proof
    val verificationCode = varchar("verificationCode", 100) // For verification purposes
    val hash = varchar("hash", 256) // Hash of the PDF content for integrity verification
}