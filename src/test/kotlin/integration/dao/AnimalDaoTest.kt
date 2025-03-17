package integration.dao

import database.TransactionProvider
import database.TestTransactionProvider
import database.dao.domain.AnimalDaoImpl
import database.schema.domain.Animals
import models.domain.Animal
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import java.time.LocalDate
import java.util.*
import kotlin.io.createTempFile

class AnimalDaoTest {
    private lateinit var testTransactionProvider: TestTransactionProvider
    private lateinit var animalDao: AnimalDaoImpl

    companion object {
        private lateinit var db: Database
        private val dbFile = createTempFile("test_db", ".db")

        @JvmStatic
        @BeforeAll
        fun setupClass() {
            println("[DEBUG_LOG] Setting up test class")
            println("[DEBUG_LOG] Using test database at: ${dbFile.absolutePath}")

            // Create a single database connection for all tests
            db = Database.connect(
                url = "jdbc:sqlite:${dbFile.absolutePath}",
                driver = "org.sqlite.JDBC"
            )
            println("[DEBUG_LOG] Database connected")

            // Initialize schema once for all tests
            transaction(db) {
                println("[DEBUG_LOG] Creating schema")
                SchemaUtils.create(Animals)
                commit()

                // Verify schema creation
                val tableExists = exec("SELECT name FROM sqlite_master WHERE type='table' AND name='Animals'") { 
                    it.next()
                }
                println("[DEBUG_LOG] Schema creation verified: $tableExists")
            }
        }

        @JvmStatic
        @AfterAll
        fun tearDownClass() {
            println("[DEBUG_LOG] Cleaning up test class")
            dbFile.delete()
            println("[DEBUG_LOG] Test database deleted")
        }
    }

    @BeforeEach
    fun setup() {
        println("[DEBUG_LOG] Setting up test")
        transaction(db) {
            // Clear all data before each test
            exec("DELETE FROM Animals")
            commit()
            println("[DEBUG_LOG] Tables cleared")

            // Verify table is empty
            val count = Animals.selectAll().count()
            println("[DEBUG_LOG] Table row count: $count")
        }

        // Initialize the DAO with the TestTransactionProvider
        testTransactionProvider = TestTransactionProvider(db)
        animalDao = AnimalDaoImpl(testTransactionProvider)
    }

    private fun createTestAnimal(
        species: String = "Cow",
        tagNumber: String = UUID.randomUUID().toString()
    ): Animal {
        return Animal(
            id = UUID.randomUUID(),
            species = species,
            breed = "Test Breed",
            gender = "Female",
            dob = LocalDate.now().minusYears(2),
            tagNumber = tagNumber,
            healthStatus = "Healthy",
            vaccinationStatus = true,
            vaccinationDate = LocalDate.now().minusMonths(6)
        )
    }

    @Test
    fun testCreateAnimal() {
        val animal = createTestAnimal()
        val createdAnimal = animalDao.createAnimal(animal)

        assertNotNull(createdAnimal.id)
        assertEquals(animal.species, createdAnimal.species)
        assertEquals(animal.breed, createdAnimal.breed)
        assertEquals(animal.gender, createdAnimal.gender)
        assertEquals(animal.tagNumber, createdAnimal.tagNumber)
        assertEquals(animal.healthStatus, createdAnimal.healthStatus)
        assertEquals(animal.vaccinationStatus, createdAnimal.vaccinationStatus)
    }

    @Test
    fun testGetAnimalById() {
        val animal = createTestAnimal()
        val createdAnimal = animalDao.createAnimal(animal)

        val fetchedAnimal = animalDao.getAnimalById(createdAnimal.id)
        assertNotNull(fetchedAnimal)
        assertEquals(createdAnimal.species, fetchedAnimal?.species)
        assertEquals(createdAnimal.breed, fetchedAnimal?.breed)
        assertEquals(createdAnimal.tagNumber, fetchedAnimal?.tagNumber)
    }

    @Test
    fun testGetAnimalByTagNumber() {
        val tagNumber = "TEST-TAG-001"
        val animal = createTestAnimal(tagNumber = tagNumber)
        animalDao.createAnimal(animal)

        val fetchedAnimal = animalDao.getAnimalByTagNumber(tagNumber)
        assertNotNull(fetchedAnimal)
        assertEquals(tagNumber, fetchedAnimal?.tagNumber)
        assertEquals(animal.species, fetchedAnimal?.species)
    }

    @Test
    fun testGetAllAnimals() {
        val animals = listOf(
            createTestAnimal(),
            createTestAnimal(),
            createTestAnimal()
        )
        animals.forEach { animalDao.createAnimal(it) }

        val fetchedAnimals = animalDao.getAllAnimals()
        assertEquals(animals.size, fetchedAnimals.size)
    }

    @Test
    fun testGetAnimalsBySpecies() {
        val species = "Sheep"
        val animals = listOf(
            createTestAnimal(species = species),
            createTestAnimal(species = species),
            createTestAnimal(species = "Goat")
        )
        animals.forEach { animalDao.createAnimal(it) }

        val fetchedAnimals = animalDao.getAnimalsBySpecies(species)
        assertEquals(2, fetchedAnimals.size)
        fetchedAnimals.forEach { 
            assertEquals(species, it.species)
        }
    }

    @Test
    fun testGetAnimalsByHealthStatus() {
        val healthStatus = "Sick"
        val animal = createTestAnimal()
        val createdAnimal = animalDao.createAnimal(animal)

        val updatedAnimal = createdAnimal.copy(healthStatus = healthStatus)
        animalDao.updateAnimal(updatedAnimal)

        val fetchedAnimals = animalDao.getAnimalsByHealthStatus(healthStatus)
        assertEquals(1, fetchedAnimals.size)
        assertEquals(healthStatus, fetchedAnimals[0].healthStatus)
    }

    @Test
    fun testUpdateAnimal() {
        val animal = createTestAnimal()
        val createdAnimal = animalDao.createAnimal(animal)

        val updatedAnimal = createdAnimal.copy(
            healthStatus = "Sick",
            vaccinationStatus = false
        )

        val updateResult = animalDao.updateAnimal(updatedAnimal)
        assertTrue(updateResult)

        val fetchedAnimal = animalDao.getAnimalById(createdAnimal.id)
        assertEquals(updatedAnimal.healthStatus, fetchedAnimal?.healthStatus)
        assertEquals(updatedAnimal.vaccinationStatus, fetchedAnimal?.vaccinationStatus)
    }

    @Test
    fun testDeleteAnimal() {
        val animal = createTestAnimal()
        val createdAnimal = animalDao.createAnimal(animal)

        val deleteResult = animalDao.deleteAnimal(createdAnimal.id)
        assertTrue(deleteResult)

        val fetchedAnimal = animalDao.getAnimalById(createdAnimal.id)
        assertNull(fetchedAnimal)
    }
}
