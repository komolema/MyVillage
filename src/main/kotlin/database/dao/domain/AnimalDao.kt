package database.dao.domain

import database.DomainTransactionProvider
import database.TransactionProvider
import database.schema.domain.Animals
import models.domain.Animal
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

interface AnimalDao {
    fun createAnimal(animal: Animal): Animal
    fun getAnimalById(id: UUID): Animal?
    fun getAnimalByTagNumber(tagNumber: String): Animal?
    fun getAllAnimals(): List<Animal>
    fun getAnimalsBySpecies(species: String): List<Animal>
    fun getAnimalsByHealthStatus(status: String): List<Animal>
    fun updateAnimal(animal: Animal): Boolean
    fun deleteAnimal(id: UUID): Boolean
}

class AnimalDaoImpl(
    private val transactionProvider: TransactionProvider = DomainTransactionProvider
) : AnimalDao {

    override fun createAnimal(animal: Animal): Animal = transactionProvider.executeTransaction {
        val id = Animals.insertAndGetId {
            it[species] = animal.species
            it[breed] = animal.breed
            it[gender] = animal.gender
            it[dob] = animal.dob
            it[tagNumber] = animal.tagNumber
            it[healthStatus] = animal.healthStatus
            it[vaccinationStatus] = animal.vaccinationStatus
            it[vaccinationDate] = animal.vaccinationDate
        }
        animal.copy(id = id.value)
    }

    override fun getAnimalById(id: UUID): Animal? = transactionProvider.executeTransaction {
        Animals.select(Animals.id eq id)
            .map { it.toAnimal() }
            .singleOrNull()
    }

    override fun getAnimalByTagNumber(tagNumber: String): Animal? = transactionProvider.executeTransaction {
        Animals.select(Animals.tagNumber eq tagNumber)
            .map { it.toAnimal() }
            .singleOrNull()
    }

    override fun getAllAnimals(): List<Animal> = transactionProvider.executeTransaction {
        Animals.selectAll()
            .map { it.toAnimal() }
    }

    override fun getAnimalsBySpecies(species: String): List<Animal> = transactionProvider.executeTransaction {
        Animals.select(Animals.species eq species)
            .map { it.toAnimal() }
    }

    override fun getAnimalsByHealthStatus(status: String): List<Animal> = transactionProvider.executeTransaction {
        Animals.select(Animals.healthStatus eq status)
            .map { it.toAnimal() }
    }

    override fun updateAnimal(animal: Animal): Boolean = transactionProvider.executeTransaction {
        Animals.update({ Animals.id eq animal.id }) {
            it[species] = animal.species
            it[breed] = animal.breed
            it[gender] = animal.gender
            it[dob] = animal.dob
            it[tagNumber] = animal.tagNumber
            it[healthStatus] = animal.healthStatus
            it[vaccinationStatus] = animal.vaccinationStatus
            it[vaccinationDate] = animal.vaccinationDate
        } > 0
    }

    override fun deleteAnimal(id: UUID): Boolean = transactionProvider.executeTransaction {
        Animals.deleteWhere { Animals.id eq id } > 0
    }

    private fun ResultRow.toAnimal(): Animal {
        return Animal(
            id = this[Animals.id].value,
            species = this[Animals.species],
            breed = this[Animals.breed],
            gender = this[Animals.gender],
            dob = this[Animals.dob],
            tagNumber = this[Animals.tagNumber],
            healthStatus = this[Animals.healthStatus],
            vaccinationStatus = this[Animals.vaccinationStatus],
            vaccinationDate = this[Animals.vaccinationDate]
        )
    }
}
