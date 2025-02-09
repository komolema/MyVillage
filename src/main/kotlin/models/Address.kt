package models

import java.util.UUID

data class Address(
    val id: UUID,
    val line: String,
    val houseNumber: String,
    val suburb: String,
    val town: String,
    val postalCode: String,
    val geoCoordinates: String?,
    val landmark: String?
) {
    companion object {
        val default = Address(
            id = UUID.randomUUID(),
            line = "",
            houseNumber = "",
            suburb = "",
            town = "",
            postalCode = "",
            geoCoordinates = null,
            landmark = null
        )
    }
}