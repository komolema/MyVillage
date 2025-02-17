package ui.screens.resident

import models.*

class ResidentWindowState(
    val residence: Residence,
    val resident: Resident,
    val qualifications: List<Qualification>,
    val dependants: List<Dependant>,
    val employmentHistory: List<Employment>,
    var mode: WindowMode

) {
    fun toggleMode() {
        mode = when (mode) {
            WindowMode.VIEW -> WindowMode.UPDATE
            WindowMode.UPDATE -> WindowMode.VIEW
            else -> mode
        }
    }
}