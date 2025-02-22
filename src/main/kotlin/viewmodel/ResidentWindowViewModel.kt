package viewmodel

import database.dao.QualificationDao
import database.dao.ResidentDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.Qualification
import models.Resident
import ui.screens.resident.ResidentWindowState
import java.util.UUID

class ResidentWindowViewModel(
    val qualificationDao: QualificationDao,
    val residentDao: ResidentDao
) {

    sealed interface Intent {
        data class LoadResident(val residentId: UUID) : Intent
        data class LoadQualifications(val residentId: UUID) : Intent
        data class CreateQualification(val newQualification: Qualification) : Intent
        data class UpdateQualification(val updatedQualification: Qualification) : Intent
        data class CreateResident(val residentState: Resident) : Intent
        data class UpdateResident(val residentState: Resident) : Intent
    }

    private val _state = MutableStateFlow(ResidentWindowState())
    val state: StateFlow<ResidentWindowState> = _state

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadResident -> loadResident(intent.residentId)
            is Intent.LoadQualifications -> loadQualifications(intent.residentId)
            is Intent.CreateQualification -> createQualification(intent.newQualification)
            is Intent.UpdateQualification -> updateQualification(intent.updatedQualification)
            is Intent.CreateResident -> createResident(intent.residentState)
            is Intent.UpdateResident -> updateResident(intent.residentState)
        }
    }

    private fun updateResident(residentState: Resident) {
        CoroutineScope(Dispatchers.IO).launch {
            residentDao.updateResident(residentState)
        }
    }

    private fun createResident(residentState: Resident) {
        CoroutineScope(Dispatchers.IO).launch {
            residentDao.createResident(residentState)
        }
    }

    private fun updateQualification(updatedQualification: Qualification) {
        CoroutineScope(Dispatchers.IO).launch {
            qualificationDao.updateQualification(updatedQualification)
        }
    }

    private fun createQualification(newQualification: Qualification) {
        CoroutineScope(Dispatchers.IO).launch {
            qualificationDao.createQualification(newQualification)
        }
    }

    private fun loadQualifications(residentId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val qualifications = qualificationDao.getQualificationsByResidentId(residentId)
            _state.update { currentState ->
                currentState.copy(
                    qualifications = qualifications,
                )
            }
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