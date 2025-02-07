package database.schema

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date

// Animal Table
object Animals : UUIDTable("Animals") {
    val species = varchar("species", 100)
    val breed = varchar("breed", 100)
    val gender = varchar("gender", 10)
    val dob = date("dob")
    val tagNumber = varchar("tagNumber", 50).uniqueIndex()
    val healthStatus = varchar("healthStatus", 100)
    val vaccinationStatus = bool("vaccinationStatus")
    val vaccinationDate = date("vaccinationDate").nullable()
}