package viewmodel

import database.dao.QualificationDao
import database.dao.ResidentDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.Qualification
import models.Resident
import ui.screens.resident.ResidentWindowState
import ui.screens.resident.WindowMode
import java.util.UUID

class ResidentWindowViewModel(
    val qualificationDao: QualificationDao,
    val residentDao: ResidentDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val viewModelScope = CoroutineScope(dispatcher)

    sealed interface Intent {
        data class LoadResident(val residentId: UUID) : Intent
        data class LoadQualifications(val residentId: UUID) : Intent
        data class CreateQualification(val newQualification: Qualification) : Intent
        data class UpdateQualification(val updatedQualification: Qualification) : Intent
        data class CreateResident(val residentState: Resident) : Intent
        data class UpdateResident(val residentState: Resident) : Intent
        data class UpdateResidentState(val residentState: Resident) : Intent

        object ToggleMode : Intent
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
            is Intent.ToggleMode -> toggleMode()
            is Intent.UpdateResidentState -> processUpdateResidentState(intent.residentState)
        }
    }

    private fun processUpdateResidentState(residentState: Resident) {
        _state.update { currentState ->
            currentState.copy(
                resident = residentState,
                error = null
            )
        }
    }

    private fun updateResident(residentState: Resident) {
        viewModelScope.launch {
            try {
                residentDao.updateResident(residentState)
                _state.update { currentState ->
                    currentState.copy(
                        resident = residentState,
                        saveSuccess = true,
                        mode = WindowMode.VIEW  // Switch to view mode after successful update
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        saveSuccess = false,
                        error = e.message ?: "Failed to save resident"
                    )
                }
            }
        }
    }

    private fun createResident(residentState: Resident) {
        viewModelScope.launch {
            try {
                residentDao.createResident(residentState)
                _state.update { currentState ->
                    currentState.copy(
                        resident = residentState,
                        saveSuccess = true,
                        mode = WindowMode.VIEW  // Switch to view mode after successful creation
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        saveSuccess = false,
                        error = e.message ?: "Failed to create resident"
                    )
                }
            }
        }
    }

    private fun updateQualification(updatedQualification: Qualification) {
        viewModelScope.launch {
            try {
                qualificationDao.updateQualification(updatedQualification)
                _state.update { currentState ->
                    currentState.copy(
                        error = null,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = e.message ?: "Failed to update qualification",
                        saveSuccess = false
                    )
                }
            }
        }
    }

    private fun createQualification(newQualification: Qualification) {
        viewModelScope.launch {
            try {
                qualificationDao.createQualification(newQualification)
                _state.update { currentState ->
                    currentState.copy(
                        error = null,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = e.message ?: "Failed to create qualification",
                        saveSuccess = false
                    )
                }
            }
        }
    }

    private fun loadQualifications(residentId: UUID) {
        viewModelScope.launch {
            try {
                val qualifications = qualificationDao.getQualificationsByResidentId(residentId)
                _state.update { currentState ->
                    currentState.copy(
                        qualifications = qualifications,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = e.message ?: "Failed to load qualifications",
                        qualifications = emptyList()
                    )
                }
            }
        }
    }

    private fun loadResident(residentId: UUID) {
        viewModelScope.launch {
            try {
                val resident = residentDao.getResidentById(residentId)
                _state.update { currentState ->
                    currentState.copy(
                        resident = resident ?: Resident.default,
                        error = null,  // Clear any previous errors
                        saveSuccess = false  // Reset save status
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = e.message ?: "Failed to load resident",
                        saveSuccess = false
                    )
                }
            }
        }
    }

    private fun toggleMode() {
        _state.update { currentState ->
            // Only allow toggling if we have a valid resident and not in NEW mode
            if (currentState.resident != Resident.default) {
                currentState.withToggledMode()
            } else {
                currentState
            }
        }
    }
}
