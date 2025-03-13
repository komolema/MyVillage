package viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import database.dao.AnimalDao
import database.dao.OwnershipDao
import database.dao.ResidentDao
import models.Animal
import models.Ownership
import ui.screens.animal.AnimalState
import java.util.*
import arrow.core.Some
import arrow.core.None
import models.expanded.AnimalExpanded
import java.time.LocalDate

class AnimalViewModel(
    private val animalDao: AnimalDao,
    private val ownershipDao: OwnershipDao,
    private val residentDao: ResidentDao
) {
    val PAGE_SIZE = 20

    sealed interface Intent {
        data class LoadAnimals(val page: Int) : Intent
        data class Search(val query: String, val page: Int) : Intent
        data class DeleteAnimal(val id: UUID) : Intent
        data class SaveAnimalChanges(val animal: Animal) : Intent
        data class OpenCreateDialog(val unit: Unit = Unit) : Intent
        data class OpenEditDialog(val animal: Animal) : Intent
        data class CloseDialog(val unit: Unit = Unit) : Intent
        data class UpdateCurrentAnimal(val animal: Animal) : Intent
        data class LoadResidents(val unit: Unit = Unit) : Intent
        data class SelectResident(val residentId: UUID) : Intent
        data class CreateAnimal(val animal: Animal) : Intent
        data class ShowDeleteConfirmation(val animalId: UUID) : Intent
        data class HideDeleteConfirmation(val unit: Unit = Unit) : Intent
        data class ConfirmDeleteAnimal(val unit: Unit = Unit) : Intent
    }

    private val _state = MutableStateFlow(AnimalState())
    val state: StateFlow<AnimalState> = _state

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadAnimals -> loadAnimals(intent.page)
            is Intent.Search -> searchAnimals(intent.query, intent.page)
            is Intent.DeleteAnimal -> deleteAnimal(intent.id)
            is Intent.SaveAnimalChanges -> saveAnimalChanges(intent.animal)
            is Intent.OpenCreateDialog -> openCreateDialog()
            is Intent.OpenEditDialog -> openEditDialog(intent.animal)
            is Intent.CloseDialog -> closeDialog()
            is Intent.UpdateCurrentAnimal -> updateCurrentAnimal(intent.animal)
            is Intent.LoadResidents -> loadResidents()
            is Intent.SelectResident -> selectResident(intent.residentId)
            is Intent.CreateAnimal -> createAnimal(intent.animal)
            is Intent.ShowDeleteConfirmation -> showDeleteConfirmation(intent.animalId)
            is Intent.HideDeleteConfirmation -> hideDeleteConfirmation()
            is Intent.ConfirmDeleteAnimal -> confirmDeleteAnimal()
        }
    }

    private fun openCreateDialog() {
        _state.update { currentState ->
            currentState.copy(
                isDialogOpen = true,
                isEditMode = false,
                currentAnimal = Animal.default,
                selectedResidentId = null
            )
        }
        loadResidents()
    }

    private fun openEditDialog(animal: Animal) {
        CoroutineScope(Dispatchers.IO).launch {
            val ownership = ownershipDao.getOwnershipsByAnimal(animal.id).firstOrNull()
            val residentId = ownership?.residentId
            
            _state.update { currentState ->
                currentState.copy(
                    isDialogOpen = true,
                    isEditMode = true,
                    currentAnimal = animal,
                    selectedResidentId = residentId
                )
            }
            loadResidents()
        }
    }

    private fun closeDialog() {
        _state.update { currentState ->
            currentState.copy(
                isDialogOpen = false,
                isEditMode = false,
                currentAnimal = Animal.default,
                selectedResidentId = null
            )
        }
    }

    private fun updateCurrentAnimal(animal: Animal) {
        _state.update { currentState ->
            currentState.copy(
                currentAnimal = animal
            )
        }
    }

    private fun loadResidents() {
        CoroutineScope(Dispatchers.IO).launch {
            val residents = residentDao.getAll(0, 100) // Get first 100 residents
            _state.update { currentState ->
                currentState.copy(
                    residents = residents
                )
            }
        }
    }

    private fun selectResident(residentId: UUID) {
        _state.update { currentState ->
            currentState.copy(
                selectedResidentId = residentId
            )
        }
    }

    private fun createAnimal(animal: Animal) {
        CoroutineScope(Dispatchers.IO).launch {
            val createdAnimal = animalDao.createAnimal(animal)
            
            // Create ownership if resident is selected
            val residentId = _state.value.selectedResidentId
            if (residentId != null) {
                val ownership = Ownership(
                    id = UUID.randomUUID(),
                    residentId = residentId,
                    animalId = createdAnimal.id,
                    paymentId = null,
                    valid = true,
                    acquisitionDate = LocalDate.now(),
                    acquisitionMethod = "Registration",
                    ownershipType = "Owner",
                    sharedWith = null
                )
                ownershipDao.createOwnership(ownership)
            }
            
            closeDialog()
            loadAnimals(0)
        }
    }

    private fun showDeleteConfirmation(animalId: UUID) {
        _state.update { currentState ->
            currentState.copy(
                showDeleteConfirmation = true,
                animalToDelete = animalId
            )
        }
    }

    private fun hideDeleteConfirmation() {
        _state.update { currentState ->
            currentState.copy(
                showDeleteConfirmation = false,
                animalToDelete = null
            )
        }
    }

    private fun confirmDeleteAnimal() {
        val animalId = _state.value.animalToDelete
        if (animalId != null) {
            deleteAnimal(animalId)
        }
        hideDeleteConfirmation()
    }

    private fun saveAnimalChanges(animal: Animal) {
        CoroutineScope(Dispatchers.IO).launch {
            // Update animal
            animalDao.updateAnimal(animal)
            
            // Update ownership
            val residentId = _state.value.selectedResidentId
            val existingOwnership = ownershipDao.getOwnershipsByAnimal(animal.id).firstOrNull()
            
            if (existingOwnership != null && residentId != null && existingOwnership.residentId != residentId) {
                // Update existing ownership with new resident
                val updatedOwnership = existingOwnership.copy(residentId = residentId)
                ownershipDao.updateOwnership(updatedOwnership)
            } else if (existingOwnership == null && residentId != null) {
                // Create new ownership
                val ownership = Ownership(
                    id = UUID.randomUUID(),
                    residentId = residentId,
                    animalId = animal.id,
                    paymentId = null,
                    valid = true,
                    acquisitionDate = LocalDate.now(),
                    acquisitionMethod = "Registration",
                    ownershipType = "Owner",
                    sharedWith = null
                )
                ownershipDao.createOwnership(ownership)
            }
            
            closeDialog()
            loadAnimals(0) // Reload the current page
        }
    }

    private fun deleteAnimal(id: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            // Delete ownership records first
            val ownerships = ownershipDao.getOwnershipsByAnimal(id)
            for (ownership in ownerships) {
                ownershipDao.deleteOwnership(ownership.id)
            }
            
            // Then delete the animal
            animalDao.deleteAnimal(id)
            loadAnimals(0)
        }
    }

    private fun searchAnimals(query: String, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val animals = animalDao.getAllAnimals() // Replace with search method when available
                .filter { animal ->
                    animal.species.contains(query, ignoreCase = true) ||
                    animal.breed.contains(query, ignoreCase = true) ||
                    animal.tagNumber.contains(query, ignoreCase = true)
                }
                .map { animal ->
                    val ownership = ownershipDao.getOwnershipsByAnimal(animal.id).firstOrNull()
                    AnimalExpanded(
                        animal = animal,
                        ownership = if (ownership != null) Some(ownership) else None
                    )
                }
            _state.update { currentState ->
                currentState.copy(
                    animals = animals,
                    isLoading = false
                )
            }
        }
    }

    private fun loadAnimals(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val animals = animalDao.getAllAnimals()
                .map { animal ->
                    val ownership = ownershipDao.getOwnershipsByAnimal(animal.id).firstOrNull()
                    AnimalExpanded(
                        animal = animal,
                        ownership = if (ownership != null) Some(ownership) else None
                    )
                }
            _state.update { currentState ->
                currentState.copy(
                    animals = animals,
                    isLoading = false
                )
            }
        }
    }
}