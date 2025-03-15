package models.expanded

import arrow.core.None
import arrow.core.Option
import models.domain.Animal
import models.domain.Ownership

data class AnimalExpanded(
    val animal: Animal,
    val ownership: Option<Ownership> = None
) {
    companion object {
        val default = AnimalExpanded(
            animal = Animal.default,
            ownership = None
        )
    }
}