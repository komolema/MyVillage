package integration.viewmodel

import database.DatabaseManager
import database.dao.audit.*
import database.dao.domain.*
import database.schema.audit.Users
import database.schema.audit.Roles
import database.schema.audit.UserRoles
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import security.PasswordUtils
import security.VillageSecurityManager
import viewmodel.LoginViewModel
import java.io.File.createTempFile
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelIntegrationTest {
    private lateinit var viewModel: LoginViewModel
    private lateinit var userDao: UserDaoImpl
    private lateinit var securityManager: VillageSecurityManager
    private val testDispatcher = StandardTestDispatcher()

    private val dbFile = createTempFile("test-audit", ".db")
    private val db = Database.connect("jdbc:sqlite:${dbFile.absolutePath}", driver = "org.sqlite.JDBC")

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Set up the database schema
        transaction(db) {
            SchemaUtils.drop(Users, Roles, UserRoles)
            SchemaUtils.create(Users, Roles, UserRoles)
            commit()
        }

        // Create the DAOs and data bags
        userDao = UserDaoImpl()

        // Create mocks for domain data bag (not used in this test)
        val domainDataBag = DomainDataBag(
            addressDao = mockk(),
            animalDao = mockk(),
            dependantDao = mockk(),
            employmentDao = mockk(),
            leadershipDao = mockk(),
            managedByDao = mockk(),
            ownershipDao = mockk(),
            paymentDao = mockk(),
            qualificationDao = mockk(),
            residenceDao = mockk(),
            residentDao = mockk(),
            resourceDao = mockk()
        )

        // Create the audit data bag with the real UserDao and mocks for other DAOs
        val auditDataBag = AuditDataBag(
            documentsGeneratedDao = mockk(),
            proofOfAddressDao = mockk(),
            userDao = userDao,
            roleDao = mockk(),
            permissionDao = mockk()
        )

        // Create the security manager
        securityManager = VillageSecurityManager(auditDataBag, domainDataBag)

        // Create the view model
        viewModel = LoginViewModel(userDao, securityManager)

        // Note: Each test will create its own test user with unique username and email
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        dbFile.delete()
    }

    @Test
    fun `test login with valid credentials`() {
        // Create a test user for this specific test
        createTestUser("testuser1", "test1@example.com")

        // Arrange
        viewModel.updateUsername("testuser1")
        viewModel.updatePassword("password123")

        // Act
        val result = viewModel.login()

        // Assert
        assertTrue(result)
        assertNull(viewModel.errorMessage)
        assertTrue(securityManager.isAuthenticated())
    }

    @Test
    fun `test login with invalid username`() {
        // Create a test user for this specific test
        createTestUser("testuser2", "test2@example.com")

        // Arrange
        viewModel.updateUsername("nonexistentuser")
        viewModel.updatePassword("password123")

        // Act
        val result = viewModel.login()

        // Assert
        assertFalse(result)
        assertEquals("Invalid username or password", viewModel.errorMessage)
        assertFalse(securityManager.isAuthenticated())
    }

    @Test
    fun `test login with invalid password`() {
        // Create a test user for this specific test
        createTestUser("testuser3", "test3@example.com")

        // Arrange
        viewModel.updateUsername("testuser3")
        viewModel.updatePassword("wrongpassword")

        // Act
        val result = viewModel.login()

        // Assert
        assertFalse(result)
        assertEquals("Invalid username or password", viewModel.errorMessage)
        assertFalse(securityManager.isAuthenticated())
    }

    @Test
    fun `test login with default admin user`() {
        // Create the default admin user
        securityManager.createAdminUser(
            username = "admin",
            password = "admin",
            firstName = "Admin",
            lastName = "User",
            email = "admin@myvillage.com"
        )

        // Clear any existing authentication
        securityManager.clearCurrentUser()

        // Arrange
        viewModel.updateUsername("admin")
        viewModel.updatePassword("admin")

        // Act
        val result = viewModel.login()

        // Assert
        assertTrue(result)
        assertNull(viewModel.errorMessage)
        assertTrue(securityManager.isAuthenticated())

        // Verify the admin has the ADMIN role
        val user = userDao.getByUsername("admin")
        assertNotNull(user)
        val roles = userDao.getUserRoles(user!!.id)
        assertTrue(roles.any { it.name == "ADMIN" })
    }

    private fun createTestUser(username: String, email: String) {
        transaction(db) {
            // Create a test user with the specified username and email
            val userId = userDao.createUser(
                username = username,
                password = "password123",
                firstName = "Test",
                lastName = "User",
                email = email
            )

            // Create default roles
            val roles = userDao.createDefaultRoles()

            // Assign the RESIDENT role to the user
            val residentRoleId = roles["RESIDENT"] ?: throw IllegalStateException("RESIDENT role not found")
            userDao.assignRoleToUser(userId, residentRoleId)

            commit()
        }
    }
}
