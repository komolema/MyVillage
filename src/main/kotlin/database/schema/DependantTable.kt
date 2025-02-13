package database.schema

import org.jetbrains.exposed.dao.id.UUIDTable

// Dependents Table
object Dependants : UUIDTable("Dependants") {
    val residentId = uuid("residentId").references(Residents.id)
    val idNumber = varchar("idNumber", 50)
    val name = varchar("name", 100)
    val surname = varchar("surname", 100)
    val gender = varchar("gender", 10)
}