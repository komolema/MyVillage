package unit.dao.audit

import database.dao.audit.PermissionDao
import database.dao.audit.PermissionDaoImpl
import database.dao.audit.RoleDao
import database.dao.audit.RoleDaoImpl
import database.schema.audit.Users
import database.schema.audit.Roles
import database.schema.audit.UserRoles
import database.schema.audit.Permissions
import database.schema.audit.RolePermissions
import database.schema.audit.ComponentPermissions
import models.audit.Permission
import models.audit.Role
import models.audit.ComponentPermission
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.UUID
import database.TestTransactionProvider

class PermissionDaoTest {

    @BeforeEach
    fun setUp() {
        // Initialize in-memory database for testing
        org.jetbrains.exposed.sql.Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

        // Create tables
        transaction {
            SchemaUtils.create(
                Users,
                Roles,
                UserRoles,
                Permissions,
                RolePermissions,
                ComponentPermissions
            )
        }
    }

    @AfterEach
    fun tearDown() {
        // Drop tables
        transaction {
            SchemaUtils.drop(
                Users,
                Roles,
                UserRoles,
                Permissions,
                RolePermissions,
                ComponentPermissions
            )
        }
    }

    @Test
    fun `test create permission`() {
        // Create a permission
        val permission = Permission(
            id = UUID.randomUUID(),
            name = "TEST_PERMISSION",
            description = "A test permission",
            action = "test"
        )

        val createdPermission = PermissionDao.create(permission)

        // Verify the permission was created
        assertNotNull(createdPermission)
        assertEquals(permission.id, createdPermission.id)
        assertEquals(permission.name, createdPermission.name)
        assertEquals(permission.description, createdPermission.description)
        assertEquals(permission.action, createdPermission.action)

        // Retrieve the permission
        val retrievedPermission = PermissionDao.getById(permission.id)

        // Verify the permission properties
        assertNotNull(retrievedPermission)
        assertEquals(permission.id, retrievedPermission?.id)
        assertEquals(permission.name, retrievedPermission?.name)
        assertEquals(permission.description, retrievedPermission?.description)
        assertEquals(permission.action, retrievedPermission?.action)
    }

    @Test
    fun `test get permission by name`() {
        // Create a permission
        val permission = Permission(
            id = UUID.randomUUID(),
            name = "TEST_PERMISSION",
            description = "A test permission",
            action = "test"
        )

        PermissionDao.create(permission)

        // Retrieve the permission by name
        val retrievedPermission = PermissionDao.getByName("TEST_PERMISSION")

        // Verify the permission was retrieved
        assertNotNull(retrievedPermission)
        assertEquals(permission.id, retrievedPermission?.id)
    }

    @Test
    fun `test get permissions by action`() {
        // Create some permissions
        val permission1 = Permission(
            id = UUID.randomUUID(),
            name = "PERMISSION_1",
            description = "Permission 1",
            action = "view"
        )

        val permission2 = Permission(
            id = UUID.randomUUID(),
            name = "PERMISSION_2",
            description = "Permission 2",
            action = "edit"
        )

        val permission3 = Permission(
            id = UUID.randomUUID(),
            name = "PERMISSION_3",
            description = "Permission 3",
            action = "view"
        )

        PermissionDao.create(permission1)
        PermissionDao.create(permission2)
        PermissionDao.create(permission3)

        // Retrieve permissions by action
        val viewPermissions = PermissionDao.getByAction("view")

        // Verify the permissions were retrieved
        assertNotNull(viewPermissions)
        assertEquals(2, viewPermissions.size)
        assertTrue(viewPermissions.any { it.name == "PERMISSION_1" })
        assertTrue(viewPermissions.any { it.name == "PERMISSION_3" })
    }

    @Test
    fun `test get all permissions`() {
        // Create some permissions
        val permission1 = Permission(
            id = UUID.randomUUID(),
            name = "PERMISSION_1",
            description = "Permission 1",
            action = "view"
        )

        val permission2 = Permission(
            id = UUID.randomUUID(),
            name = "PERMISSION_2",
            description = "Permission 2",
            action = "edit"
        )

        PermissionDao.create(permission1)
        PermissionDao.create(permission2)

        // Retrieve all permissions
        val permissions = PermissionDao.getAll()

        // Verify the permissions were retrieved
        assertNotNull(permissions)
        assertTrue(permissions.size >= 2)
        assertTrue(permissions.any { it.name == "PERMISSION_1" })
        assertTrue(permissions.any { it.name == "PERMISSION_2" })
    }

    @Test
    fun `test update permission`() {
        // Create a permission
        val permission = Permission(
            id = UUID.randomUUID(),
            name = "TEST_PERMISSION",
            description = "A test permission",
            action = "test"
        )

        PermissionDao.create(permission)

        // Update the permission
        val updatedPermission = permission.copy(
            name = "UPDATED_PERMISSION",
            description = "An updated test permission",
            action = "updated"
        )

        PermissionDao.update(updatedPermission)

        // Retrieve the updated permission
        val retrievedPermission = PermissionDao.getById(permission.id)

        // Verify the permission was updated
        assertNotNull(retrievedPermission)
        assertEquals(updatedPermission.name, retrievedPermission?.name)
        assertEquals(updatedPermission.description, retrievedPermission?.description)
        assertEquals(updatedPermission.action, retrievedPermission?.action)
    }

    @Test
    fun `test delete permission`() {
        // Create a permission
        val permission = Permission(
            id = UUID.randomUUID(),
            name = "TEST_PERMISSION",
            description = "A test permission",
            action = "test"
        )

        PermissionDao.create(permission)

        // Verify the permission exists
        assertNotNull(PermissionDao.getById(permission.id))

        // Delete the permission
        val result = PermissionDao.delete(permission.id)

        // Verify the permission was deleted
        assertTrue(result)
        assertNull(PermissionDao.getById(permission.id))
    }

    @Test
    fun `test assign permission to role`() {
        // Create a role
        val role = Role(
            id = UUID.randomUUID(),
            name = "TEST_ROLE",
            description = "A test role",
            isSystem = false
        )

        RoleDao.create(role)

        // Create a permission
        val permission = Permission(
            id = UUID.randomUUID(),
            name = "TEST_PERMISSION",
            description = "A test permission",
            action = "test"
        )

        PermissionDao.create(permission)

        // Assign the permission to the role
        val rolePermission = PermissionDao.assignPermissionToRole(role.id, permission.id)

        // Verify the permission was assigned
        assertNotNull(rolePermission)
        assertEquals(role.id, rolePermission.roleId)
        assertEquals(permission.id, rolePermission.permissionId)

        // Get role permissions
        val rolePermissions = PermissionDao.getRolePermissions(role.id)

        // Verify the role has the permission
        assertNotNull(rolePermissions)
        assertEquals(1, rolePermissions.size)
        assertEquals(permission.id, rolePermissions[0].id)
    }

    @Test
    fun `test remove permission from role`() {
        // Create a role
        val role = Role(
            id = UUID.randomUUID(),
            name = "TEST_ROLE",
            description = "A test role",
            isSystem = false
        )

        RoleDao.create(role)

        // Create a permission
        val permission = Permission(
            id = UUID.randomUUID(),
            name = "TEST_PERMISSION",
            description = "A test permission",
            action = "test"
        )

        PermissionDao.create(permission)

        // Assign the permission to the role
        PermissionDao.assignPermissionToRole(role.id, permission.id)

        // Verify the role has the permission
        val rolePermissions = PermissionDao.getRolePermissions(role.id)
        assertEquals(1, rolePermissions.size)

        // Remove the permission from the role
        val result = PermissionDao.removePermissionFromRole(role.id, permission.id)

        // Verify the permission was removed
        assertTrue(result)
        val updatedRolePermissions = PermissionDao.getRolePermissions(role.id)
        assertEquals(0, updatedRolePermissions.size)
    }

    @Test
    fun `test create component permission`() {
        // Create a permission
        val permission = Permission(
            id = UUID.randomUUID(),
            name = "TEST_PERMISSION",
            description = "A test permission",
            action = "test"
        )

        PermissionDao.create(permission)

        // Create a component permission
        val componentPermission = ComponentPermission(
            id = UUID.randomUUID(),
            componentId = "test-component",
            componentType = "button",
            permissionId = permission.id,
            description = "A test component permission"
        )

        val createdComponentPermission = PermissionDao.createComponentPermission(componentPermission)

        // Verify the component permission was created
        assertNotNull(createdComponentPermission)
        assertEquals(componentPermission.id, createdComponentPermission.id)
        assertEquals(componentPermission.componentId, createdComponentPermission.componentId)
        assertEquals(componentPermission.componentType, createdComponentPermission.componentType)
        assertEquals(componentPermission.permissionId, createdComponentPermission.permissionId)
        assertEquals(componentPermission.description, createdComponentPermission.description)

        // Get component permissions
        val componentPermissions = PermissionDao.getComponentPermissions("test-component")

        // Verify the component has the permission
        assertNotNull(componentPermissions)
        assertEquals(1, componentPermissions.size)
        assertEquals(permission.id, componentPermissions[0].id)
    }

    @Test
    fun `test remove component permission`() {
        // Create a permission
        val permission = Permission(
            id = UUID.randomUUID(),
            name = "TEST_PERMISSION",
            description = "A test permission",
            action = "test"
        )

        PermissionDao.create(permission)

        // Create a component permission
        val componentPermission = ComponentPermission(
            id = UUID.randomUUID(),
            componentId = "test-component",
            componentType = "button",
            permissionId = permission.id,
            description = "A test component permission"
        )

        PermissionDao.createComponentPermission(componentPermission)

        // Verify the component has the permission
        val componentPermissions = PermissionDao.getComponentPermissions("test-component")
        assertEquals(1, componentPermissions.size)

        // Remove the component permission
        val result = PermissionDao.removeComponentPermission(componentPermission.id)

        // Verify the component permission was removed
        assertTrue(result)
        val updatedComponentPermissions = PermissionDao.getComponentPermissions("test-component")
        assertEquals(0, updatedComponentPermissions.size)
    }
}
