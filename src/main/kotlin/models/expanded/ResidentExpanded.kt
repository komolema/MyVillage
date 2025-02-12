package models.expanded

import arrow.core.None
import arrow.core.Option
import models.Address
import models.Residence
import models.Resident

data class ResidentExpanded(
    val resident: Resident,
    val address: Option<Address>,
    val residence: Option<Residence>,
    val dependents: List<Resident> = emptyList()
){
    companion object {
        val default = ResidentExpanded(
            resident = Resident.default,
            address = None,
            residence = None)
    }
}
