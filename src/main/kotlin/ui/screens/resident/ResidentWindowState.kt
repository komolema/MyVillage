package ui.screens.resident

import models.*

data class ResidentWindowState(
    val residence: Residence = Residence.default,
    val resident: Resident = Resident.default,
    val qualifications: List<Qualification> = emptyList(),
    val dependants: List<Dependant> = emptyList(),
    val employmentHistory: List<Employment> = emptyList(),
    var mode: WindowMode = WindowMode.VIEW
) {
    fun toggleMode() {
        mode = when (mode) {
            WindowMode.VIEW -> WindowMode.UPDATE
            WindowMode.UPDATE -> WindowMode.VIEW
            else -> mode
        }
    }

    companion object {
        val default = ResidentWindowState()
    }
}