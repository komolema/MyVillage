package database.schema.domain

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date

// Payment Table
object Payments : UUIDTable("Payments") {
    val date = date("date")
    val method = varchar("method", 50)
    val note = text("note").nullable()
    val price = decimal("price", 10, 2)
}