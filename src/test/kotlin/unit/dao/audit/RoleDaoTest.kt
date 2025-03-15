package unit.dao.audit

import database.dao.audit.RoleDao
import database.dao.audit.UserDao
import database.schema.audit.Users
import database.schema.audit.Roles
import database.schema.audit.UserRoles
import models.audit.Role
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.UUID

class RoleDaoTest {
    
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
    fun `test create role`() {
        // Create a role
        val role = Role(
            id = UUID.randomUUID(),
            name = "TEST_ROLE",
            description = "A test role",
            isSystem = false
        )
        
        val createdRole = RoleDao.create(role)
        
        // Verify the role was created
        assertNotNull(createdRole)
        assertEquals(role.id, createdRole.id)
        assertEquals(role.name, createdRole.name)
        assertEquals(role.description, createdRole.description)
        assertEquals(role.isSystem, createdRole.isSystem)
        
        // Retrieve the role
        val retrievedRole = RoleDao.getById(role.id)
        
        // Verify the role properties
        assertNotNull(retrievedRole)
        assertEquals(role.id, retrievedRole?.id)
        assertEquals(role.name, retrievedRole?.name)
        assertEquals(role.description, retrievedRole?.description)
        assertEquals(role.isSystem, retrievedRole?.isSystem)
    }
    
    @Test
    fun `test get role by name`() {
        // Create a role
        val role = Role(
            id = UUID.randomUUID(),
            name = "TEST_ROLE",
            description = "A test role",
            isSystem = false
        )
        
        RoleDao.create(role)
        
        // Retrieve the role by name
        val retrievedRole = RoleDao.getByName("TEST_ROLE")
        
        // Verify the role was retrieved
        assertNotNull(retrievedRole)
        assertEquals(role.id, retrievedRole?.id)
    }
    
    @Test
    fun `test get all roles`() {
        // Create some roles
        val role1 = Role(
            id = UUID.randomUUID(),
            name = "ROLE_1",
            description = "Role 1",
            isSystem = false
        )
        
        val role2 = Role(
            id = UUID.randomUUID(),
            name = "ROLE_2",
            description = "Role 2",
            isSystem = true
        )
        
        RoleDao.create(role1)
        RoleDao.create(role2)
        
        // Retrieve all roles
        val roles = RoleDao.getAll()
        
        // Verify the roles were retrieved
        assertNotNull(roles)
        assertTrue(roles.size >= 2)
        assertTrue(roles.any { it.name == "ROLE_1" })
        assertTrue(roles.any { it.name == "ROLE_2" })
    }
    
    @Test
    fun `test update role`() {
        // Create a role
        val role = Role(
            id = UUID.randomUUID(),
            name = "TEST_ROLE",
            description = "A test role",
            isSystem = false
        )
        
        RoleDao.create(role)
        
        // Update the role
        val updatedRole = role.copy(
            name = "UPDATED_ROLE",
            description = "An updated test role",
            isSystem = true
        )
        
        RoleDao.update(updatedRole)
        
        // Retrieve the updated role
        val retrievedRole = RoleDao.getById(role.id)
        
        // Verify the role was updated
        assertNotNull(retrievedRole)
        assertEquals(updatedRole.name, retrievedRole?.name)
        assertEquals(updatedRole.description, retrievedRole?.description)
        assertEquals(updatedRole.isSystem, retrievedRole?.isSystem)
    }
    
    @Test
    fun `test delete role`() {
        // Create a role
        val role = Role(
            id = UUID.randomUUID(),
            name = "TEST_ROLE",
            description = "A test role",
            isSystem = false
        )
        
        RoleDao.create(role)
        
        // Verify the role exists
        assertNotNull(RoleDao.getById(role.id))
        
        // Delete the role
        val result = RoleDao.delete(role.id)
        
        // Verify the role was deleted
        assertTrue(result)
        assertNull(RoleDao.getById(role.id))
    }
    
    @Test
    fun `test cannot delete system role`() {
        // Create a system role
        val role = Role(
            id = UUID.randomUUID(),
            name = "SYSTEM_ROLE",
            description = "A system role",
            isSystem = true
        )
        
        RoleDao.create(role)
        
        // Verify the role exists
        assertNotNull(RoleDao.getById(role.id))
        
        // Try to delete the role
        val result = RoleDao.delete(role.id)
        
        // Verify the role was not deleted
        assertFalse(result)
        assertNotNull(RoleDao.getById(role.id))
    }
    
    @Test
    fun `test get users with role`() {
        // Create a user
        val userId = UserDao.createUser(
            username = "testuser",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com"
        )
        
        // Create a role
        val role = Role(
            id = UUID.randomUUID(),
            name = "TEST_ROLE",
            description = "A test role",
            isSystem = false
        )
        
        RoleDao.create(role)
        
        // Assign the role to the user
        UserDao.assignRoleToUser(userId, role.id)
        
        // Get users with the role
        val users = RoleDao.getUsersWithRole(role.id)
        
        // Verify the user has the role
        assertNotNull(users)
        assertEquals(1, users.size)
        assertEquals(userId, users[0])
    }
}