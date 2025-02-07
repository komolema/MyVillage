package database.schema

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date

// Ownership Table
object Ownerships : UUIDTable("Ownerships") {
    val residentId = uuid("residentId").references(Residents.id)
    val animalId = uuid("animalId").references(Animals.id)
    val paymentId = uuid("paymentId").references(Payments.id).nullable()
    val valid = bool("valid")
    val acquisitionDate = date("acquisitionDate")
    val acquisitionMethod = varchar("acquisitionMethod", 100)
    val ownershipType = varchar("ownershipType", 50)
    val sharedWith = varchar("sharedWith", 200).nullable() // Comma-separated IDs or details
}
