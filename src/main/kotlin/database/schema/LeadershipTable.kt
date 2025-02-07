package database.schema

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date

// Leadership Table
object Leadership : UUIDTable("Leadership") {
    val name = varchar("name", 100)
    val role = varchar("role", 100)
    val startDate = date("startDate")
    val endDate = date("endDate").nullable()
    val villageName = varchar("villageName", 100)
}