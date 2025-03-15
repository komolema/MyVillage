package database.schema.audit

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Table for storing user authentication information.
 */
object Users : UUIDTable("Users") {
    val username = varchar("username", 100).uniqueIndex()
    val passwordHash = varchar("passwordHash", 256) // Hashed password
    val salt = varchar("salt", 256) // Salt used for password hashing
    val firstName = varchar("firstName", 100)
    val lastName = varchar("lastName", 100)
    val email = varchar("email", 100).uniqueIndex()
    val phoneNumber = varchar("phoneNumber", 20).nullable()
    val isActive = bool("isActive").default(true)
    val createdAt = datetime("createdAt")
    val lastLogin = datetime("lastLogin").nullable()
    val residentId = uuid("residentId").nullable() // Optional link to Residents table in domain database
}