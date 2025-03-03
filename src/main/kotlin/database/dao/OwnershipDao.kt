package database.dao

import database.schema.Ownerships
import models.Ownership
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
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
}

class OwnershipDaoImpl : OwnershipDao {
    override fun createOwnership(ownership: Ownership): Ownership = transaction {
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

    override fun getOwnershipById(id: UUID): Ownership? = transaction {
        Ownerships.select(Ownerships.id eq id)
            .map { it.toOwnership() }
            .singleOrNull()
    }

    override fun getAllOwnerships(): List<Ownership> = transaction {
        Ownerships.selectAll()
            .map { it.toOwnership() }
    }

    override fun getOwnershipsByResident(residentId: UUID): List<Ownership> = transaction {
        Ownerships.select(Ownerships.residentId eq residentId)
            .map { it.toOwnership() }
    }

    override fun getOwnershipsByAnimal(animalId: UUID): List<Ownership> = transaction {
        Ownerships.select(Ownerships.animalId eq animalId)
            .map { it.toOwnership() }
    }

    override fun getValidOwnerships(): List<Ownership> = transaction {
        Ownerships.select(Ownerships.valid eq true)
            .map { it.toOwnership() }
    }

    override fun updateOwnership(ownership: Ownership): Boolean = transaction {
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

    override fun deleteOwnership(id: UUID): Boolean = transaction {
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