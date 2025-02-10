package ui.screens.resident

import models.Resident

data class ResidentState(val residents: List<Resident> = emptyList(), val isLoading: Boolean = true) {
    companion object {
        val default = ResidentState()
    }
}