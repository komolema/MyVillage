package database.schema.domain

import org.jetbrains.exposed.dao.id.UUIDTable

// Resource Table
object Resources : UUIDTable("Resources") {
    val type = varchar("type", 100)
    val location = varchar("location", 200)
}
