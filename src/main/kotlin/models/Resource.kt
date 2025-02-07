package models

import java.util.*

data class Resource(
    val id: UUID,
    val type: String,
    val location: String
)