package ui.screens.resident

import models.Dependant
import models.Employment
import models.Qualification
import models.Residence

class ResidentWindowState(
    val residentState: ResidentState,
    val residence: Residence,
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