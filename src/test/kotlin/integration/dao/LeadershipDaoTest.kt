package integration.dao

import database.TestTransactionProvider
import database.TransactionProvider
import database.dao.domain.LeadershipDaoImpl
import database.schema.domain.Leadership
import models.domain.Leadership as LeadershipModel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.util.*

class LeadershipDaoTest {
    private lateinit var db: Database
    private lateinit var testTransactionProvider: TestTransactionProvider
    private lateinit var leadershipDao: LeadershipDaoImpl

    @BeforeEach
    fun setup() {
        db = Database.connect("jdbc:sqlite::memory:", driver = "org.sqlite.JDBC")
        transaction(db) {
            SchemaUtils.create(Leadership)
        }
        testTransactionProvider = TestTransactionProvider(db)
        leadershipDao = LeadershipDaoImpl(testTransactionProvider)
    }

    private fun createTestLeadership(villageName: String = "Test Village"): LeadershipModel {
        return LeadershipModel(
            id = UUID.randomUUID(),
            name = "Test Leader",
            role = "Chief",
            startDate = LocalDate.now().minusYears(1),
            endDate = LocalDate.now(),
            villageName = villageName
        )
    }

    @Test
    fun testCreateLeadership() {
        val leadership = createTestLeadership()
        val createdLeadership = leadershipDao.createLeadership(leadership)
        assertNotNull(createdLeadership.id)
        assertEquals(leadership.name, createdLeadership.name)
        assertEquals(leadership.role, createdLeadership.role)
    }

    @Test
    fun testGetLeadershipById() {
        val leadership = createTestLeadership()
        val createdLeadership = leadershipDao.createLeadership(leadership)

        val fetchedLeadership = leadershipDao.getLeadershipById(createdLeadership.id)
        assertNotNull(fetchedLeadership)
        assertEquals(createdLeadership.name, fetchedLeadership?.name)
        assertEquals(createdLeadership.role, fetchedLeadership?.role)
    }

    @Test
    fun testGetAllLeadership() {
        val leaderships = listOf(
            createTestLeadership("Village 1"),
            createTestLeadership("Village 2"),
            createTestLeadership("Village 3")
        )
        leaderships.forEach { leadershipDao.createLeadership(it) }

        val fetchedLeaderships = leadershipDao.getAllLeadership()
        assertEquals(leaderships.size, fetchedLeaderships.size)
        leaderships.forEach { leadership ->
            assertTrue(fetchedLeaderships.any { it.villageName == leadership.villageName })
        }
    }

    @Test
    fun testGetLeadershipByVillage() {
        val villageName = "Test Village"
        val leaderships = listOf(
            createTestLeadership(villageName),
            createTestLeadership(villageName),
            createTestLeadership("Other Village")
        )
        leaderships.forEach { leadershipDao.createLeadership(it) }

        val fetchedLeaderships = leadershipDao.getLeadershipByVillage(villageName)
        assertEquals(2, fetchedLeaderships.size)
        fetchedLeaderships.forEach { 
            assertEquals(villageName, it.villageName)
        }
    }

    @Test
    fun testUpdateLeadership() {
        val leadership = createTestLeadership()
        val createdLeadership = leadershipDao.createLeadership(leadership)

        val updatedLeadership = createdLeadership.copy(
            name = "Updated Leader",
            role = "Updated Role"
        )

        val updateResult = leadershipDao.updateLeadership(updatedLeadership)
        assertTrue(updateResult)

        val fetchedLeadership = leadershipDao.getLeadershipById(createdLeadership.id)
        assertEquals(updatedLeadership.name, fetchedLeadership?.name)
        assertEquals(updatedLeadership.role, fetchedLeadership?.role)
    }

    @Test
    fun testDeleteLeadership() {
        val leadership = createTestLeadership()
        val createdLeadership = leadershipDao.createLeadership(leadership)

        val deleteResult = leadershipDao.deleteLeadership(createdLeadership.id)
        assertTrue(deleteResult)

        val fetchedLeadership = leadershipDao.getLeadershipById(createdLeadership.id)
        assertNull(fetchedLeadership)
    }
}
