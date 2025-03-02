package database.dao

import models.Dependant
import java.util.*

interface DependantDao {
    suspend fun getDependantsByResidentId(residentId: UUID): List<Dependant>
    suspend fun createDependant(dependant: Dependant): Dependant
    suspend fun updateDependant(dependant: Dependant): Dependant
    suspend fun deleteDependant(dependantId: UUID)
    fun getDependentsByResidentId(id: UUID): List<Dependant>
}

class DependantDaoImpl : DependantDao {
    private val dependants = mutableMapOf<UUID, Dependant>()

    override suspend fun getDependantsByResidentId(residentId: UUID): List<Dependant> {
        return dependants.values.filter { it.residentId == residentId }
    }

    override suspend fun createDependant(dependant: Dependant): Dependant {
        dependants[dependant.id] = dependant
        return dependant
    }

    override suspend fun updateDependant(dependant: Dependant): Dependant {
        dependants[dependant.id] = dependant
        return dependant
    }

    override suspend fun deleteDependant(dependantId: UUID) {
        dependants.remove(dependantId)
    }

    override fun getDependentsByResidentId(id: UUID): List<Dependant> {
        return dependants.values.filter { it.residentId == id }
    }
}