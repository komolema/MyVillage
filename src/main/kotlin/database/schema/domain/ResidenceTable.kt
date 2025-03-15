package database.schema.domain

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date

// Residence Table
object Residences : UUIDTable("Residences") {
    val residentId = uuid("residentId").references(Residents.id)
    val addressId = uuid("addressId").references(Addresses.id)
    val occupationDate = date("occupationDate")
}