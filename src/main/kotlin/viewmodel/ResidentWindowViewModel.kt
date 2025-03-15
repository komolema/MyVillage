package viewmodel

import database.dao.domain.DomainDataBag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.domain.Dependant
import models.domain.Employment
import models.domain.Qualification
import models.domain.Resident
import models.domain.Residence
import models.domain.Address
import ui.screens.resident.ResidentWindowState
import ui.screens.resident.WindowMode
import java.util.UUID

class ResidentWindowViewModel(
    private val domainDataBag: DomainDataBag,
    private val initialMode: WindowMode = WindowMode.VIEW,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val viewModelScope = CoroutineScope(dispatcher)

    sealed interface Intent {
        data class LoadResident(val residentId: UUID) : Intent
        data class LoadQualifications(val residentId: UUID) : Intent
        data class LoadDependants(val residentId: UUID) : Intent
        data class LoadEmployment(val residentId: UUID) : Intent
        data class CreateQualification(val newQualification: Qualification) : Intent
        data class UpdateQualification(val updatedQualification: Qualification) : Intent
        data class DeleteQualification(val qualificationId: UUID) : Intent
        data class CreateDependant(val newDependant: Dependant) : Intent
        data class UpdateDependant(val updatedDependant: Dependant) : Intent
        data class DeleteDependant(val dependantId: UUID) : Intent
        data class CreateEmployment(val newEmployment: Employment) : Intent
        data class UpdateEmployment(val updatedEmployment: Employment) : Intent
        data class DeleteEmployment(val employmentId: UUID) : Intent
        data class CreateResident(val residentState: Resident) : Intent
        data class UpdateResident(val residentState: Resident) : Intent
        data class UpdateResidentState(val residentState: Resident) : Intent
        data class LoadResidence(val residentId: UUID) : Intent
        data class CreateResidence(val residence: Residence, val address: Address) : Intent
        data class UpdateResidence(val residence: Residence, val address: Address) : Intent
        data class DeleteResidence(val residenceId: UUID, val addressId: UUID) : Intent

        data object ToggleMode : Intent
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
            is Intent.LoadResidence -> loadResidence(intent.residentId)
            is Intent.CreateResidence -> createResidence(intent.residence, intent.address)
            is Intent.UpdateResidence -> updateResidence(intent.residence, intent.address)
            is Intent.DeleteResidence -> deleteResidence(intent.residenceId, intent.addressId)
            is Intent.LoadEmployment -> loadEmployment(intent.residentId)
            is Intent.CreateEmployment -> createEmployment(intent.newEmployment)
            is Intent.UpdateEmployment -> updateEmployment(intent.updatedEmployment)
            is Intent.DeleteEmployment -> deleteEmployment(intent.employmentId)
        }
    }

    private fun loadResidence(residentId: UUID) {
        viewModelScope.launch {
            try {
                val residence = domainDataBag.residenceDao.getResidenceByResidentId(residentId)
                val address = if (residence != null) {
                    domainDataBag.addressDao.getById(residence.addressId)
                } else null

                _state.update { currentState ->
                    currentState.copy(
                        residence = residence,
                        address = address,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to load residence: ${e.message}"
                    )
                }
            }
        }
    }

    private fun createResidence(residence: Residence, address: Address) {
        viewModelScope.launch {
            try {
                domainDataBag.addressDao.create(address)
                domainDataBag.residenceDao.createResidence(residence)

                _state.update { currentState ->
                    currentState.copy(
                        residence = residence,
                        address = address,
                        error = null,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to create residence: ${e.message}",
                        saveSuccess = false
                    )
                }
            }
        }
    }

    private fun updateResidence(residence: Residence, address: Address) {
        viewModelScope.launch {
            try {
                domainDataBag.addressDao.update(address)
                domainDataBag.residenceDao.updateResidence(residence)

                _state.update { currentState ->
                    currentState.copy(
                        residence = residence,
                        address = address,
                        error = null,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to update residence: ${e.message}",
                        saveSuccess = false
                    )
                }
            }
        }
    }

    private fun deleteResidence(residenceId: UUID, addressId: UUID) {
        viewModelScope.launch {
            try {
                domainDataBag.residenceDao.deleteResidence(residenceId)
                domainDataBag.addressDao.delete(addressId)

                _state.update { currentState ->
                    currentState.copy(
                        residence = null,
                        address = null,
                        error = null,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to delete residence: ${e.message}",
                        saveSuccess = false
                    )
                }
            }
        }
    }

    private fun deleteQualification(qualificationId: UUID) {
        viewModelScope.launch {
            try {
                domainDataBag.qualificationDao.deleteQualification(qualificationId)
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
                val dependants = domainDataBag.dependantDao.getDependantsByResidentId(residentId)
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
                val createdDependant = domainDataBag.dependantDao.createDependant(newDependant)
                val updatedDependants = domainDataBag.dependantDao.getDependantsByResidentId(createdDependant.residentId)
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
                domainDataBag.dependantDao.updateDependant(updatedDependant)
                val updatedDependants = domainDataBag.dependantDao.getDependantsByResidentId(updatedDependant.residentId)
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
                domainDataBag.dependantDao.deleteDependant(dependantId)
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
                domainDataBag.residentDao.updateResident(residentState)
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
                domainDataBag.residentDao.createResident(residentState)
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
                if (domainDataBag.qualificationDao.updateQualification(updatedQualification)) {
                    val updatedQualifications = domainDataBag.qualificationDao.getQualificationsByResidentId(updatedQualification.residentId)
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
                val createdQualification = domainDataBag.qualificationDao.createQualification(newQualification)
                val updatedQualifications = domainDataBag.qualificationDao.getQualificationsByResidentId(createdQualification.residentId)
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
                val qualifications = domainDataBag.qualificationDao.getQualificationsByResidentId(residentId)
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
                val resident = domainDataBag.residentDao.getResidentById(residentId)
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

    private fun loadEmployment(residentId: UUID) {
        viewModelScope.launch {
            try {
                val employment = domainDataBag.employmentDao.getEmploymentByResidentId(residentId)
                _state.update { currentState ->
                    currentState.copy(
                        employmentHistory = employment,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to load employment history: ${e.message}",
                        employmentHistory = emptyList()
                    )
                }
            }
        }
    }

    private fun createEmployment(newEmployment: Employment) {
        viewModelScope.launch {
            try {
                val createdEmployment = domainDataBag.employmentDao.createEmployment(newEmployment)
                val updatedEmployment = domainDataBag.employmentDao.getEmploymentByResidentId(createdEmployment.residentId)
                _state.update { currentState ->
                    currentState.copy(
                        employmentHistory = updatedEmployment,
                        error = null,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to create employment record: ${e.message}",
                        saveSuccess = false
                    )
                }
            }
        }
    }

    private fun updateEmployment(updatedEmployment: Employment) {
        viewModelScope.launch {
            try {
                domainDataBag.employmentDao.updateEmployment(updatedEmployment)
                val updatedHistory = domainDataBag.employmentDao.getEmploymentByResidentId(updatedEmployment.residentId)
                _state.update { currentState ->
                    currentState.copy(
                        employmentHistory = updatedHistory,
                        error = null,
                        saveSuccess = true
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to update employment record: ${e.message}",
                        saveSuccess = false
                    )
                }
            }
        }
    }

    private fun deleteEmployment(employmentId: UUID) {
        viewModelScope.launch {
            try {
                domainDataBag.employmentDao.deleteEmployment(employmentId)
                _state.update { currentState ->
                    currentState.copy(
                        employmentHistory = currentState.employmentHistory.filter { it.id != employmentId },
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to delete employment record: ${e.message}"
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
