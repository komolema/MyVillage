package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import security.VillageSecurityManager

/**
 * A component that only renders its content if the current user has the required permission.
 * 
 * @param componentId The ID of the component, used for permission checking
 * @param action The action being performed (e.g., "view", "edit", "create", "delete")
 * @param modifier The modifier to be applied to the component
 * @param fallback The content to display if the user doesn't have permission (optional)
 * @param content The content to display if the user has permission
 */
@Composable
fun SecureComponent(
    componentId: String,
    action: String,
    modifier: Modifier = Modifier,
    fallback: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val securityManager = koinInject<VillageSecurityManager>()

    if (securityManager.hasPermission(componentId, action)) {
        content()
    } else {
        fallback?.invoke()
    }
}

/**
 * A component that only renders its content if the current user has the required role.
 * 
 * @param role The role required to view the content
 * @param modifier The modifier to be applied to the component
 * @param fallback The content to display if the user doesn't have the required role (optional)
 * @param content The content to display if the user has the required role
 */
@Composable
fun SecureRoleComponent(
    role: String,
    modifier: Modifier = Modifier,
    fallback: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val securityManager = koinInject<VillageSecurityManager>()
    if (securityManager.hasRole(role)) {
        content()
    } else {
        fallback?.invoke()
    }
}