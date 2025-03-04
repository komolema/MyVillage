package unit.dao

import models.Resident
import models.Qualification
import models.Dependant
import models.Address
import models.Residence
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.util.UUID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils
import database.schema.*
import database.dao.*

class ResidentDaoTest {
    private val qualificationDao = QualificationDaoImpl()
    private val dependantDao = DependantDaoImpl()
    private val addressDao = AddressDaoImpl()
    private val residenceDao = ResidenceDaoImpl()
    private val residentDao = ResidentDaoImpl(residenceDao, dependantDao)

    @Test
    fun `test resident deletion should delete all related data`() {
        // Setup test database
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction {
            // Create tables
            SchemaUtils.create(Residents, Qualifications, Dependants, Addresses, Residences)
        }

        // Create test data
        val residentId = UUID.randomUUID()
        val resident = Resident(
            id = residentId,
            idNumber = "123456",
            firstName = "John",
            lastName = "Doe",
            dob = LocalDate.now(),
            gender = "Male",
            phoneNumber = "1234567890",
            email = "john@example.com"
        )

        val qualification = Qualification(
            id = UUID.randomUUID(),
            residentId = residentId,
            name = "Test Qualification",
            institution = "Test Institution",
            nqfLevel = 5,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            city = "Test City"
        )

        val dependant = Dependant(
            id = UUID.randomUUID(),
            residentId = residentId,
            idNumber = "654321",
            name = "Jane",
            surname = "Doe",
            gender = "Female"
        )

        val addressId = UUID.randomUUID()
        val address = Address(
            id = addressId,
            line = "Test Street",
            houseNumber = "123",
            suburb = "Test Suburb",
            town = "Test City",
            postalCode = "12345",
            geoCoordinates = null,
            landmark = null
        )

        val residence = Residence(
            id = UUID.randomUUID(),
            residentId = residentId,
            addressId = addressId,
            occupationDate = LocalDate.now()
        )

        // Insert test data
        transaction {
            residentDao.createResident(resident)
            qualificationDao.createQualification(qualification)
            dependantDao.createDependant(dependant)
            addressDao.create(address)
            residenceDao.createResidence(residence)
        }

        // Verify data was created
        transaction {
            assertNotNull(residentDao.getResidentById(residentId))
            assertTrue(qualificationDao.getQualificationsByResidentId(residentId).isNotEmpty())
            assertTrue(dependantDao.getDependantsByResidentId(residentId).isNotEmpty())
            assertNotNull(residenceDao.getAddressByResidentId(residentId))
            assertNotNull(residenceDao.getResidenceByResidentId(residentId))
        }

        // Delete resident
        transaction {
            residentDao.delete(residentId)
        }

        // Verify all related data is deleted
        transaction {
            assertNull(residentDao.getResidentById(residentId))
            assertTrue(qualificationDao.getQualificationsByResidentId(residentId).isEmpty())
            assertTrue(dependantDao.getDependantsByResidentId(residentId).isEmpty())
            assertNull(residenceDao.getAddressByResidentId(residentId))
            assertNull(residenceDao.getResidenceByResidentId(residentId))
        }
    }
}