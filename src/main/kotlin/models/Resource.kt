package models

import java.util.UUID

data class Resource(
    val id: UUID,
    val type: String,
    val location: String
)