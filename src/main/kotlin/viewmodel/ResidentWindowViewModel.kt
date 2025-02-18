package viewmodel

import database.dao.QualificationDao
import database.dao.ResidentDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.Resident
import ui.screens.resident.ResidentWindowState
import java.util.UUID

class ResidentWindowViewModel(
    val qualificationDao: QualificationDao,
    val residentDao: ResidentDao
) {

    sealed interface Intent {
        data class LoadResident(val residentId: UUID) : Intent
    }

    private val _state = MutableStateFlow(ResidentWindowState())
    val state: StateFlow<ResidentWindowState> = _state

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadResident -> loadResident(intent.residentId)
        }
    }

    private fun loadResident(residentId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val resident = residentDao.getResidentById(residentId)
            _state.update { currentState ->
                currentState.copy(
                    resident = resident ?: Resident.default,
                )
            }
        }
    }
}