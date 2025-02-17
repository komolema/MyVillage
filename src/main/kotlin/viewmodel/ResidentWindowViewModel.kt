package viewmodel

import database.dao.QualificationDao
import database.dao.ResidentDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ui.screens.resident.ResidentListState

class ResidentWindowViewModel(
    val qualificationDao: QualificationDao,
    val residentDao: ResidentDao
) {

    private val _residentState = MutableStateFlow(ResidentListState())
    val state: StateFlow<ResidentListState> = _state
}