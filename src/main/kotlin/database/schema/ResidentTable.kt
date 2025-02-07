package database.schema

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date

// Residents Table
object Residents : UUIDTable("Residents") {
    val firstName = varchar("firstName", 100)
    val lastName = varchar("lastName", 100)
    val dob = date("dob")
    val gender = varchar("gender", 10)
    val idNumber = varchar("idNumber", 50).uniqueIndex()
    val phoneNumber = varchar("phoneNumber", 20).nullable()
    val email = varchar("email", 100).nullable()
}