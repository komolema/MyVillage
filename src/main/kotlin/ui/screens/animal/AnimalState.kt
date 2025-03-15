package ui.screens.animal

import models.domain.Animal
import models.domain.Resident
import models.expanded.AnimalExpanded
import java.util.UUID

data class AnimalState(
    val animals: List<AnimalExpanded> = emptyList(),
    val isLoading: Boolean = true,
    val totalItems: Int = 0,
    val isDialogOpen: Boolean = false,
    val isEditMode: Boolean = false,
    val currentAnimal: Animal = Animal.default,
    val residents: List<Resident> = emptyList(),
    val selectedResidentId: UUID? = null,
    val showDeleteConfirmation: Boolean = false,
    val animalToDelete: UUID? = null
) {
    companion object {
        val default = AnimalState()
    }
}
