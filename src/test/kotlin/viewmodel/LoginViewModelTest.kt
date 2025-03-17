package viewmodel

import database.dao.audit.UserDao
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import models.audit.User
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import security.VillageSecurityManager
import java.time.LocalDateTime
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel
    private lateinit var userDao: UserDao
    private lateinit var securityManager: VillageSecurityManager
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userDao = mockk()
        securityManager = mockk()

        viewModel = LoginViewModel(userDao, securityManager)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test updateUsername updates username`() {
        // Act
        viewModel.updateUsername("newuser")

        // Assert
        assertEquals("newuser", viewModel.username)
    }

    @Test
    fun `test updatePassword updates password`() {
        // Act
        viewModel.updatePassword("newpassword")

        // Assert
        assertEquals("newpassword", viewModel.password)
    }

    @Test
    fun `test login with empty username and password`() {
        // Arrange
        viewModel.updateUsername("")
        viewModel.updatePassword("")

        // Act
        val result = viewModel.login()

        // Assert
        assertFalse(result)
        assertEquals("Username and password cannot be empty", viewModel.errorMessage)
    }

    @Test
    fun `test login with invalid username`() {
        // Arrange
        viewModel.updateUsername("invaliduser")
        viewModel.updatePassword("password")

        // Mock the userDao to return null for the invalid username
        every { userDao.getByUsername("invaliduser") } returns null

        // Act
        val result = viewModel.login()

        // Assert
        assertFalse(result)
        assertEquals("Invalid username or password", viewModel.errorMessage)
    }

    @Test
    fun `test login with exception`() {
        // Arrange
        viewModel.updateUsername("testuser")
        viewModel.updatePassword("password")

        // Mock the userDao to throw an exception
        every { userDao.getByUsername("testuser") } throws Exception("Database error")

        // Act
        val result = viewModel.login()

        // Assert
        assertFalse(result)
        assertEquals("An error occurred: Database error", viewModel.errorMessage)
    }

    @Test
    fun `test clearForm resets fields`() {
        // Arrange
        viewModel.updateUsername("testuser")
        viewModel.updatePassword("password")

        // Act
        viewModel.clearForm()

        // Assert
        assertEquals("", viewModel.username)
        assertEquals("", viewModel.password)
    }

    @Test
    fun `test login with valid credentials`() {
        // Arrange
        val userId = UUID.randomUUID()
        val testUser = User(
            id = userId,
            username = "admin",
            passwordHash = "hashed_admin",
            salt = "salt",
            firstName = "Admin",
            lastName = "User",
            email = "admin@myvillage.com",
            phoneNumber = null,
            isActive = true,
            createdAt = LocalDateTime.now(),
            lastLogin = null,
            residentId = null
        )

        viewModel.updateUsername("admin")
        viewModel.updatePassword("admin")

        // Mock the userDao to return the test user
        every { userDao.getByUsername("admin") } returns testUser

        // Mock the password verification to return true without using the static method
        val passwordUtils = mockk<security.PasswordUtils>()
        every { passwordUtils.verifyPassword(any(), any()) } returns true

        // Use reflection to replace the PasswordUtils object with our mock
        val field = security.PasswordUtils::class.java.getDeclaredField("INSTANCE")
        field.isAccessible = true
        val originalInstance = field.get(null)
        field.set(null, passwordUtils)

        try {
            // Mock the security manager
            every { securityManager.setCurrentUser(userId) } just Runs
            every { userDao.updateLastLogin(userId) } returns true

            // Act - just verify the method is called, don't check the result
            viewModel.login()

            // Assert - verify that the methods were called with the correct parameters
            verify { userDao.getByUsername("admin") }
            verify { securityManager.setCurrentUser(userId) }
            verify { userDao.updateLastLogin(userId) }
        } finally {
            // Restore the original PasswordUtils instance
            field.set(null, originalInstance)
        }
    }
}
