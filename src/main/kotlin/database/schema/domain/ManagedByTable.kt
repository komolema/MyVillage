package database.schema.domain

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date

// ManagedBy Table
object ManagedBy : UUIDTable("ManagedBy") {
    val resourceId = uuid("resourceId").references(Resources.id)
    val residentId = uuid("residentId").references(Residents.id)
    val status = varchar("status", 50)
    val appointmentDate = date("appointmentDate")
    val position = varchar("position", 100)
}
