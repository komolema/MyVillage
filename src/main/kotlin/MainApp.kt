import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.WindowPlacement
import di.appModule
import org.koin.core.context.startKoin
import ui.navigation.AppNavigation
import database.DatabaseManager
import di.viewModelModule
import theme.BlueButtonColor

@Composable
@Preview
fun App() {
    MaterialTheme(
        colors = lightColors(
            primary = BlueButtonColor
        )
    ) {
        AppNavigation()
    }
}

fun main() {
    startKoin {
        modules(appModule)
        modules(viewModelModule)
    }
    // Initialize databases
    DatabaseManager.initializeDatabases()

    // Initialize security system
    val securityManager = org.koin.core.context.GlobalContext.get().get<security.VillageSecurityManager>()
    securityManager.initialize()

    // Create default admin user if this is the first startup
    if (database.DatabaseManager.isFirstStartup()) {
        securityManager.createAdminUser(
            username = "admin",
            password = "admin",
            firstName = "Admin",
            lastName = "User",
            email = "admin@myvillage.com"
        )
    }

    application {
        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
        Window(onCloseRequest = ::exitApplication, state = windowState) {
            App()
        }
    }
}
