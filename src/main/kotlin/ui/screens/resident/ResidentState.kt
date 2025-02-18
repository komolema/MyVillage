package ui.screens.resident

import models.expanded.ResidentExpanded

data class ResidentState(
    val residents: List<ResidentExpanded> = emptyList(),
    val isLoading: Boolean = true,
    val totalItems: Int = 0
) {

    companion object {
        val default = ResidentState()
    }
}