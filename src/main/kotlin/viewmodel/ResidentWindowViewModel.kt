package viewmodel

import database.dao.DependantDao
import database.dao.QualificationDao
import database.dao.ResidentDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.Dependant
import models.Qualification
import models.Resident
import ui.screens.resident.ResidentWindowState
import ui.screens.resident.WindowMode
import java.util.UUID

class ResidentWindowViewModel(
    val qualificationDao: QualificationDao,
    val residentDao: ResidentDao,
    val dependantDao: DependantDao,
    private val initialMode: WindowMode = WindowMode.VIEW,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val viewModelScope = CoroutineScope(dispatcher)

    sealed interface Intent {
        data class LoadResident(val residentId: UUID) : Intent
        data class LoadQualifications(val residentId: UUID) : Intent
        data class LoadDependants(val residentId: UUID) : Intent
        data class CreateQualification(val newQualification: Qualification) : Intent
        data class UpdateQualification(val updatedQualification: Qualification) : Intent
        data class DeleteQualification(val qualificationId: UUID) : Intent
        data class CreateDependant(val newDependant: Dependant) : Intent
        data class UpdateDependant(val updatedDependant: Dependant) : Intent
        data class DeleteDependant(val dependantId: UUID) : Intent
        data class CreateResident(val residentState: Resident) : Intent
        data class UpdateResident(val residentState: Resident) : Intent
        data class UpdateResidentState(val residentState: Resident) : Intent

        object ToggleMode : Intent
    }

    private val _state = MutableStateFlow(ResidentWindowState(mode = initialMode))
    val state: StateFlow<ResidentWindowState> = _state

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadResident -> loadResident(intent.residentId)
            is Intent.LoadQualifications -> loadQualifications(intent.residentId)
            is Intent.LoadDependants -> loadDependants(intent.residentId)
            is Intent.CreateQualification -> createQualification(intent.newQualification)
            is Intent.UpdateQualification -> updateQualification(intent.updatedQualification)
            is Intent.DeleteQualification -> deleteQualification(intent.qualificationId)
            is Intent.CreateDependant -> createDependant(intent.newDependant)
            is Intent.UpdateDependant -> updateDependant(intent.updatedDependant)
            is Intent.DeleteDependant -> deleteDependant(intent.dependantId)
            is Intent.CreateResident -> createResident(intent.residentState)
            is Intent.UpdateResident -> updateResident(intent.residentState)
            is Intent.ToggleMode -> toggleMode()
            is Intent.UpdateResidentState -> processUpdateResidentState(intent.residentState)
        }
    }

    private fun deleteQualification(qualificationId: UUID) {
        viewModelScope.launch {
            try {
                qualificationDao.deleteQualification(qualificationId)
                _state.update { currentState ->
                    currentState.copy(
                        qualifications = currentState.qualifications.filter { it.id != qualificationId },
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to delete qualification: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadDependants(residentId: UUID) {
        viewModelScope.launch {
            try {
                val dependants = dependantDao.getDependantsByResidentId(residentId)
                _state.update { currentState ->
                    currentState.copy(
                        dependants = dependants,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to load dependants: ${e.message}"
                    )
                }
            }
        }
    }

    private fun createDependant(newDependant: Dependant) {
        viewModelScope.launch {
            try {
                val createdDependant = dependantDao.createDependant(newDependant)
                val updatedDependants = dependantDao.getDependantsByResidentId(createdDependant.residentId)
                _state.update { currentState ->
                    currentState.copy(
                        dependants = updatedDependants,
                        error = null,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to create dependant: ${e.message}"
                    )
                }
            }
        }
    }

    private fun updateDependant(updatedDependant: Dependant) {
        viewModelScope.launch {
            try {
                val savedDependant = dependantDao.updateDependant(updatedDependant)
                val updatedDependants = dependantDao.getDependantsByResidentId(savedDependant.residentId)
                _state.update { currentState ->
                    currentState.copy(
                        dependants = updatedDependants,
                        error = null,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to update dependant: ${e.message}"
                    )
                }
            }
        }
    }

    private fun deleteDependant(dependantId: UUID) {
        viewModelScope.launch {
            try {
                dependantDao.deleteDependant(dependantId)
                _state.update { currentState ->
                    currentState.copy(
                        dependants = currentState.dependants.filter { it.id != dependantId },
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to delete dependant: ${e.message}"
                    )
                }
            }
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
                if (qualificationDao.updateQualification(updatedQualification)) {
                    val updatedQualifications = qualificationDao.getQualificationsByResidentId(updatedQualification.residentId)
                    _state.update { currentState ->
                        currentState.copy(
                            qualifications = updatedQualifications,
                            error = null,
                            saveSuccess = true
                        )
                    }
                } else {
                    throw Exception("Failed to update qualification")
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
                val createdQualification = qualificationDao.createQualification(newQualification)
                val updatedQualifications = qualificationDao.getQualificationsByResidentId(createdQualification.residentId)
                _state.update { currentState ->
                    currentState.copy(
                        qualifications = updatedQualifications,
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
