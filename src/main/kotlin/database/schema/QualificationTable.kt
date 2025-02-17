package database.schema

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date

// Qualification Table
object Qualifications : UUIDTable("Qualifications") {
    val residentId = uuid("residentId").references(Residents.id)
    val name = varchar("name", 100)
    val institution = varchar("institution", 100)
    val nqfLevel = integer("nqfLevel")
    val startDate = date("startDate")
    val endDate = date("endDate").nullable()
    val city = varchar("city", 100)
}