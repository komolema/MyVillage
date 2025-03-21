package database.schema.domain

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date

// Employment Table
object EmploymentTable : UUIDTable("Employment") {
    val residentId = uuid("residentId").references(Residents.id)
    val employer = varchar("employer", 100)
    val role = varchar("role", 100)
    val startDate = date("startDate")
    val endDate = date("endDate").nullable()
}
