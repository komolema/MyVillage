package models

import java.util.*

data class Resource(
    val id: UUID,
    val type: String,
    val location: String
) {
    companion object {
        val default = Resource(
            id = UUID.randomUUID(),
            type = "",
            location = ""
        )
    }
}