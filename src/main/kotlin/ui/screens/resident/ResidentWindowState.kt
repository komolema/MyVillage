package ui.screens.resident

import models.*

data class ResidentWindowState(
    val residence: Residence? = null,
    val address: Address? = null,
    val resident: Resident = Resident.default,
    val qualifications: List<Qualification> = emptyList(),
    val dependants: List<Dependant> = emptyList(),
    val employmentHistory: List<Employment> = emptyList(),
    val mode: WindowMode = WindowMode.VIEW,
    val saveSuccess: Boolean = false,
    val error: String? = null
) {
    fun withToggledMode(): ResidentWindowState {
        val newMode = when (mode) {
            WindowMode.VIEW -> WindowMode.UPDATE
            WindowMode.UPDATE -> WindowMode.VIEW
            else -> mode
        }
        return this.copy(mode = newMode)
    }

    companion object {
        val default = ResidentWindowState()
    }
}
