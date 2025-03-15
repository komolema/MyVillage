package models.expanded

import arrow.core.None
import arrow.core.Option
import models.domain.Address
import models.domain.Dependant
import models.domain.Residence
import models.domain.Resident

data class ResidentExpanded(
    val resident: Resident,
    val address: Option<Address>,
    val residence: Option<Residence>,
    val dependants: List<Dependant> = emptyList()
){
    companion object {
        val default = ResidentExpanded(
            resident = Resident.default,
            address = None,
            residence = None)
    }
}
