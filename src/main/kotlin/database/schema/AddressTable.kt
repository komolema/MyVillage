package database.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.dao.id.UUIDTable





// Address Table
object Addresses : UUIDTable("Addresses") {
    val line = varchar("line", 200)
    val houseNumber = varchar("houseNumber", 20)
    val suburb = varchar("suburb", 100)
    val town = varchar("town", 100)
    val postalCode = varchar("postalCode", 20)
    val geoCoordinates = varchar("geoCoordinates", 50).nullable()
    val landmark = varchar("landmark", 200).nullable()
}









