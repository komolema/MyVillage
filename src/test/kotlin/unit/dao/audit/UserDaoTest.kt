package unit.dao.audit

import database.DatabaseManager
import database.dao.audit.UserDao
import database.dao.audit.UserDaoImpl
import database.schema.audit.Users
import database.schema.audit.Roles
import database.schema.audit.UserRoles
import models.audit.User
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.util.UUID

class UserDaoTest {
    private val userDao = UserDaoImpl()
    @BeforeEach
    fun setUp() {
        // Initialize in-memory database for testing
        org.jetbrains.exposed.sql.Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        
        // Create tables
        transaction {
            SchemaUtils.create(Users, Roles, UserRoles)
        }
    }
    
    @AfterEach
    fun tearDown() {
        // Drop tables
        transaction {
            SchemaUtils.drop(Users, Roles, UserRoles)
        }
    }
    
    @Test
    fun `test create user`() {
        // Create a user
        val userId = userDao.createUser(
            username = "testuser",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com",
            phoneNumber = "1234567890"
        )
        
        // Verify the user was created
        assertNotNull(userId)
        
        // Retrieve the user
        val user = userDao.getById(userId)
        
        // Verify the user properties
        assertNotNull(user)
        assertEquals("testuser", user?.username)
        assertEquals("Test", user?.firstName)
        assertEquals("User", user?.lastName)
        assertEquals("test@example.com", user?.email)
        assertEquals("1234567890", user?.phoneNumber)
        assertTrue(user?.isActive ?: false)
    }
    
    @Test
    fun `test get user by username`() {
        // Create a user
        val userId = userDao.createUser(
            username = "testuser",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com"
        )
        
        // Retrieve the user by username
        val user = userDao.getByUsername("testuser")
        
        // Verify the user was retrieved
        assertNotNull(user)
        assertEquals(userId, user?.id)
    }
    
    @Test
    fun `test get user by email`() {
        // Create a user
        val userId = userDao.createUser(
            username = "testuser",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com"
        )
        
        // Retrieve the user by email
        val user = userDao.getByEmail("test@example.com")
        
        // Verify the user was retrieved
        assertNotNull(user)
        assertEquals(userId, user?.id)
    }
    
    @Test
    fun `test update user`() {
        // Create a user
        val userId = userDao.createUser(
            username = "testuser",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com"
        )
        
        // Retrieve the user
        val user = userDao.getById(userId)
        assertNotNull(user)
        
        // Update the user
        val updatedUser = user!!.copy(
            firstName = "Updated",
            lastName = "Name",
            email = "updated@example.com"
        )
        
        userDao.update(updatedUser)
        
        // Retrieve the updated user
        val retrievedUser = userDao.getById(userId)
        
        // Verify the user was updated
        assertNotNull(retrievedUser)
        assertEquals("Updated", retrievedUser?.firstName)
        assertEquals("Name", retrievedUser?.lastName)
        assertEquals("updated@example.com", retrievedUser?.email)
    }
    
    @Test
    fun `test delete user`() {
        // Create a user
        val userId = userDao.createUser(
            username = "testuser",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com"
        )
        
        // Verify the user exists
        assertNotNull(userDao.getById(userId))
        
        // Delete the user
        val result = userDao.delete(userId)
        
        // Verify the user was deleted
        assertTrue(result)
        assertNull(userDao.getById(userId))
    }
    
    @Test
    fun `test create default roles`() {
        // Create default roles
        val roles = userDao.createDefaultRoles()
        
        // Verify the roles were created
        assertNotNull(roles)
        assertTrue(roles.isNotEmpty())
        assertTrue(roles.containsKey("ADMIN"))
        assertTrue(roles.containsKey("CHIEF"))
        assertTrue(roles.containsKey("RESIDENT"))
        assertTrue(roles.containsKey("GUEST"))
    }
    
    @Test
    fun `test assign role to user`() {
        // Create a user
        val userId = userDao.createUser(
            username = "testuser",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com"
        )
        
        // Create default roles
        val roles = userDao.createDefaultRoles()
        val adminRoleId = roles["ADMIN"]!!
        
        // Assign the ADMIN role to the user
        val userRole = userDao.assignRoleToUser(userId, adminRoleId)
        
        // Verify the role was assigned
        assertNotNull(userRole)
        assertEquals(userId, userRole.userId)
        assertEquals(adminRoleId, userRole.roleId)
        
        // Get user roles
        val userRoles = userDao.getUserRoles(userId)
        
        // Verify the user has the ADMIN role
        assertNotNull(userRoles)
        assertEquals(1, userRoles.size)
        assertEquals("ADMIN", userRoles[0].name)
    }
}