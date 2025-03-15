package database.dao.domain

import database.DomainTransactionProvider
import database.TransactionProvider
import database.schema.domain.Ownerships
import models.domain.Ownership
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

interface OwnershipDao {
    fun createOwnership(ownership: Ownership): Ownership
    fun getOwnershipById(id: UUID): Ownership?
    fun getAllOwnerships(): List<Ownership>
    fun getOwnershipsByResident(residentId: UUID): List<Ownership>
    fun getOwnershipsByAnimal(animalId: UUID): List<Ownership>
    fun getValidOwnerships(): List<Ownership>
    fun updateOwnership(ownership: Ownership): Boolean
    fun deleteOwnership(id: UUID): Boolean

    companion object {
        private val impl = OwnershipDaoImpl()

        fun createOwnership(ownership: Ownership): Ownership = 
            impl.createOwnership(ownership)
        fun getOwnershipById(id: UUID): Ownership? = 
            impl.getOwnershipById(id)
        fun getAllOwnerships(): List<Ownership> = 
            impl.getAllOwnerships()
        fun getOwnershipsByResident(residentId: UUID): List<Ownership> = 
            impl.getOwnershipsByResident(residentId)
        fun getOwnershipsByAnimal(animalId: UUID): List<Ownership> = 
            impl.getOwnershipsByAnimal(animalId)
        fun getValidOwnerships(): List<Ownership> = 
            impl.getValidOwnerships()
        fun updateOwnership(ownership: Ownership): Boolean = 
            impl.updateOwnership(ownership)
        fun deleteOwnership(id: UUID): Boolean = 
            impl.deleteOwnership(id)
    }
}

class OwnershipDaoImpl(private val transactionProvider: TransactionProvider = DomainTransactionProvider) : OwnershipDao {
    override fun createOwnership(ownership: Ownership): Ownership = transactionProvider.executeTransaction {
        val id = Ownerships.insertAndGetId {
            it[residentId] = ownership.residentId
            it[animalId] = ownership.animalId
            it[paymentId] = ownership.paymentId
            it[valid] = ownership.valid
            it[acquisitionDate] = ownership.acquisitionDate
            it[acquisitionMethod] = ownership.acquisitionMethod
            it[ownershipType] = ownership.ownershipType
            it[sharedWith] = ownership.sharedWith
        }
        ownership.copy(id = id.value)
    }

    override fun getOwnershipById(id: UUID): Ownership? = transactionProvider.executeTransaction {
        Ownerships.selectAll()
            .where { Ownerships.id eq id }
            .map { it.toOwnership() }
            .singleOrNull()
    }

    override fun getAllOwnerships(): List<Ownership> = transactionProvider.executeTransaction {
        Ownerships.selectAll()
            .map { it.toOwnership() }
    }

    override fun getOwnershipsByResident(residentId: UUID): List<Ownership> = transactionProvider.executeTransaction {
        Ownerships.selectAll()
            .where { Ownerships.residentId eq residentId }
            .map { it.toOwnership() }
    }

    override fun getOwnershipsByAnimal(animalId: UUID): List<Ownership> = transactionProvider.executeTransaction {
        Ownerships.selectAll()
            .where { Ownerships.animalId eq animalId }
            .map { it.toOwnership() }
    }

    override fun getValidOwnerships(): List<Ownership> = transactionProvider.executeTransaction {
        Ownerships.selectAll()
            .where { Ownerships.valid eq true }
            .map { it.toOwnership() }
    }

    override fun updateOwnership(ownership: Ownership): Boolean = transactionProvider.executeTransaction {
        Ownerships.update({ Ownerships.id eq ownership.id }) {
            it[residentId] = ownership.residentId
            it[animalId] = ownership.animalId
            it[paymentId] = ownership.paymentId
            it[valid] = ownership.valid
            it[acquisitionDate] = ownership.acquisitionDate
            it[acquisitionMethod] = ownership.acquisitionMethod
            it[ownershipType] = ownership.ownershipType
            it[sharedWith] = ownership.sharedWith
        } > 0
    }

    override fun deleteOwnership(id: UUID): Boolean = transactionProvider.executeTransaction {
        Ownerships.deleteWhere { Ownerships.id eq id } > 0
    }

    private fun ResultRow.toOwnership(): Ownership {
        return Ownership(
            id = this[Ownerships.id].value,
            residentId = this[Ownerships.residentId],
            animalId = this[Ownerships.animalId],
            paymentId = this[Ownerships.paymentId],
            valid = this[Ownerships.valid],
            acquisitionDate = this[Ownerships.acquisitionDate],
            acquisitionMethod = this[Ownerships.acquisitionMethod],
            ownershipType = this[Ownerships.ownershipType],
            sharedWith = this[Ownerships.sharedWith]
        )
    }
}
