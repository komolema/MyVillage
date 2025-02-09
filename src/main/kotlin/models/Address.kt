package models

import java.util.UUID
import java.time.LocalDate

data class Address(
    val id: UUID,
    val line: String,
    val houseNumber: String,
    val suburb: String,
    val town: String,
    val postalCode: String,
    val geoCoordinates: String?,
    val landmark: String?
)














