package ui.screens.resident

import models.Qualification

class ResidentWindowState(
    val residentState: ResidentState,
    val qualifications: List<Qualification>,
    // Other tab states...
    val mode: WindowMode
) {
    fun toggleMode() {
        mode = when (mode) {
            WindowMode.VIEW -> WindowMode.UPDATE
            WindowMode.UPDATE -> WindowMode.VIEW
            else -> mode
        }
    }
}