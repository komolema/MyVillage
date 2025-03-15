package database.dao.audit

import database.DatabaseManager
import database.TransactionProvider
import database.AuditTransactionProvider
import database.schema.audit.Permissions
import database.schema.audit.RolePermissions
import database.schema.audit.ComponentPermissions
import models.audit.Permission
import models.audit.RolePermission
import models.audit.ComponentPermission
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

/**
 * Interface for the Permission Data Access Object.
 */
interface PermissionDao {
    fun create(permission: Permission): Permission
    fun getById(id: UUID): Permission?
    fun getByName(name: String): Permission?
    fun getByAction(action: String): List<Permission>
    fun getAll(): List<Permission>
    fun update(permission: Permission): Permission
    fun delete(id: UUID): Boolean

    fun assignPermissionToRole(roleId: UUID, permissionId: UUID): RolePermission
    fun removePermissionFromRole(roleId: UUID, permissionId: UUID): Boolean
    fun getRolePermissions(roleId: UUID): List<Permission>

    fun createComponentPermission(componentPermission: ComponentPermission): ComponentPermission
    fun getComponentPermissions(componentId: String): List<Permission>
    fun removeComponentPermission(id: UUID): Boolean

    companion object : PermissionDao {
        private var impl: PermissionDao = PermissionDaoImpl()

        /**
         * Set a custom implementation for testing.
         */
        fun setImplementation(implementation: PermissionDao) {
            impl = implementation
        }

        override fun create(permission: Permission): Permission = impl.create(permission)
        override fun getById(id: UUID): Permission? = impl.getById(id)
        override fun getByName(name: String): Permission? = impl.getByName(name)
        override fun getByAction(action: String): List<Permission> = impl.getByAction(action)
        override fun getAll(): List<Permission> = impl.getAll()
        override fun update(permission: Permission): Permission = impl.update(permission)
        override fun delete(id: UUID): Boolean = impl.delete(id)

        override fun assignPermissionToRole(roleId: UUID, permissionId: UUID): RolePermission = 
            impl.assignPermissionToRole(roleId, permissionId)
        override fun removePermissionFromRole(roleId: UUID, permissionId: UUID): Boolean = 
            impl.removePermissionFromRole(roleId, permissionId)
        override fun getRolePermissions(roleId: UUID): List<Permission> = 
            impl.getRolePermissions(roleId)

        override fun createComponentPermission(componentPermission: ComponentPermission): ComponentPermission = 
            impl.createComponentPermission(componentPermission)
        override fun getComponentPermissions(componentId: String): List<Permission> = 
            impl.getComponentPermissions(componentId)
        override fun removeComponentPermission(id: UUID): Boolean = 
            impl.removeComponentPermission(id)
    }
}

/**
 * Implementation of the PermissionDao interface.
 * Provides CRUD operations for permissions, role permissions, and component permissions.
 */
class PermissionDaoImpl(private val transactionProvider: TransactionProvider = AuditTransactionProvider) : PermissionDao {
    /**
     * Creates a new permission in the database.
     *
     * @param permission The permission to create
     * @return The created permission
     */
    override fun create(permission: Permission): Permission = transactionProvider.executeTransaction {
        Permissions.insert {
            it[id] = permission.id
            it[name] = permission.name
            it[description] = permission.description
            it[action] = permission.action
        }
        permission
    }

    /**
     * Retrieves a permission by its ID.
     *
     * @param id The ID of the permission to retrieve
     * @return The permission if found, null otherwise
     */
    override fun getById(id: UUID): Permission? = transactionProvider.executeTransaction {
        Permissions.selectAll()
            .where { Permissions.id eq id }
            .limit(1)
            .map { toPermission(it) }
            .singleOrNull()
    }

    /**
     * Retrieves a permission by its name.
     *
     * @param name The name of the permission to retrieve
     * @return The permission if found, null otherwise
     */
    override fun getByName(name: String): Permission? = transactionProvider.executeTransaction {
        Permissions.selectAll()
            .where { Permissions.name eq name }
            .limit(1)
            .map { toPermission(it) }
            .singleOrNull()
    }

    /**
     * Retrieves permissions by their action.
     *
     * @param action The action of the permissions to retrieve
     * @return A list of permissions with the specified action
     */
    override fun getByAction(action: String): List<Permission> = transactionProvider.executeTransaction {
        Permissions.selectAll()
            .where { Permissions.action eq action }
            .map { toPermission(it) }
    }

    /**
     * Retrieves all permissions.
     *
     * @return A list of all permissions
     */
    override fun getAll(): List<Permission> = transactionProvider.executeTransaction {
        Permissions.selectAll()
            .map { toPermission(it) }
    }

    /**
     * Updates an existing permission in the database.
     *
     * @param permission The permission to update
     * @return The updated permission
     */
    override fun update(permission: Permission): Permission = transactionProvider.executeTransaction {
        Permissions.update({ Permissions.id eq permission.id }) {
            it[name] = permission.name
            it[description] = permission.description
            it[action] = permission.action
        }
        permission
    }

    /**
     * Deletes a permission from the database.
     *
     * @param id The ID of the permission to delete
     * @return true if the permission was deleted, false otherwise
     */
    override fun delete(id: UUID): Boolean = transactionProvider.executeTransaction {
        // First delete any role permission mappings
        RolePermissions.deleteWhere { RolePermissions.permissionId eq id }

        // Then delete any component permission mappings
        ComponentPermissions.deleteWhere { ComponentPermissions.permissionId eq id }

        // Finally delete the permission
        Permissions.deleteWhere { Permissions.id eq id } > 0
    }

    /**
     * Assigns a permission to a role.
     *
     * @param roleId The ID of the role
     * @param permissionId The ID of the permission to assign
     * @return The created role permission mapping
     */
    override fun assignPermissionToRole(roleId: UUID, permissionId: UUID): RolePermission = transactionProvider.executeTransaction {
        // Check if the role already has this permission
        val existingRolePermission = RolePermissions.selectAll()
            .where { (RolePermissions.roleId eq roleId) and (RolePermissions.permissionId eq permissionId) }
            .limit(1)
            .map { toRolePermission(it) }
            .singleOrNull()

        if (existingRolePermission != null) {
            return@executeTransaction existingRolePermission
        }

        // Create the role permission mapping
        val rolePermissionId = RolePermissions.insert {
            it[id] = UUID.randomUUID()
            it[RolePermissions.roleId] = roleId
            it[RolePermissions.permissionId] = permissionId
        } get RolePermissions.id

        RolePermission(rolePermissionId.value, roleId, permissionId)
    }

    /**
     * Removes a permission from a role.
     *
     * @param roleId The ID of the role
     * @param permissionId The ID of the permission to remove
     * @return true if the permission was removed, false otherwise
     */
    override fun removePermissionFromRole(roleId: UUID, permissionId: UUID): Boolean = transactionProvider.executeTransaction {
        RolePermissions.deleteWhere { 
            (RolePermissions.roleId eq roleId) and (RolePermissions.permissionId eq permissionId) 
        } > 0
    }

    /**
     * Retrieves all permissions assigned to a role.
     *
     * @param roleId The ID of the role
     * @return A list of permissions assigned to the role
     */
    override fun getRolePermissions(roleId: UUID): List<Permission> = transactionProvider.executeTransaction {
        (RolePermissions innerJoin Permissions)
            .selectAll()
            .where { RolePermissions.roleId eq roleId }
            .map { toPermission(it) }
    }

    /**
     * Creates a new component permission in the database.
     *
     * @param componentPermission The component permission to create
     * @return The created component permission
     */
    override fun createComponentPermission(componentPermission: ComponentPermission): ComponentPermission = transactionProvider.executeTransaction {
        ComponentPermissions.insert {
            it[id] = componentPermission.id
            it[componentId] = componentPermission.componentId
            it[componentType] = componentPermission.componentType
            it[permissionId] = componentPermission.permissionId
            it[description] = componentPermission.description
        }
        componentPermission
    }

    /**
     * Retrieves all permissions for a component.
     *
     * @param componentId The ID of the component
     * @return A list of permissions for the component
     */
    override fun getComponentPermissions(componentId: String): List<Permission> = transactionProvider.executeTransaction {
        (ComponentPermissions innerJoin Permissions)
            .selectAll()
            .where { ComponentPermissions.componentId eq componentId }
            .map { toPermission(it) }
    }

    /**
     * Removes a component permission.
     *
     * @param id The ID of the component permission to remove
     * @return true if the component permission was removed, false otherwise
     */
    override fun removeComponentPermission(id: UUID): Boolean = transactionProvider.executeTransaction {
        ComponentPermissions.deleteWhere { ComponentPermissions.id eq id } > 0
    }

    /**
     * Converts a ResultRow to a Permission object.
     */
    private fun toPermission(row: ResultRow): Permission {
        val entityId = row[Permissions.id] as EntityID<UUID>
        return Permission(
            id = entityId.value,
            name = row[Permissions.name],
            description = row[Permissions.description],
            action = row[Permissions.action]
        )
    }

    /**
     * Converts a ResultRow to a RolePermission object.
     */
    private fun toRolePermission(row: ResultRow): RolePermission {
        val entityId = row[RolePermissions.id] as EntityID<UUID>
        return RolePermission(
            id = entityId.value,
            roleId = (row[RolePermissions.roleId] as EntityID<UUID>).value,
            permissionId = (row[RolePermissions.permissionId] as EntityID<UUID>).value
        )
    }

    /**
     * Converts a ResultRow to a ComponentPermission object.
     */
    private fun toComponentPermission(row: ResultRow): ComponentPermission {
        val entityId = row[ComponentPermissions.id] as EntityID<UUID>
        return ComponentPermission(
            id = entityId.value,
            componentId = row[ComponentPermissions.componentId],
            componentType = row[ComponentPermissions.componentType],
            permissionId = (row[ComponentPermissions.permissionId] as EntityID<UUID>).value,
            description = row[ComponentPermissions.description]
        )
    }
}
