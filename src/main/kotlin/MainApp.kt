import database.DatabaseManager
import di.appModule
import di.viewModelModule
import javafx.application.Application
import javafx.stage.Stage
import org.koin.core.context.startKoin
import security.VillageSecurityManager
import ui.navigation.NavigationManager

class MainApp : Application() {
    override fun start(stage: Stage) {
        // Initialize Koin
        startKoin {
            modules(appModule)
            modules(viewModelModule)
        }

        // Initialize databases
        DatabaseManager.initializeDatabases()

        // Initialize security system
        val securityManager = org.koin.core.context.GlobalContext.get().get<VillageSecurityManager>()
        securityManager.initialize()

        // Create default admin user if this is the first startup
        if (DatabaseManager.isFirstStartup()) {
            securityManager.createAdminUser(
                username = "admin",
                password = "admin",
                firstName = "Admin",
                lastName = "User",
                email = "admin@myvillage.com"
            )
        }

        // Set up the primary stage
        stage.title = "My Village"
        stage.isMaximized = true

        // Initialize the navigation manager with the primary stage
        val navigationManager = NavigationManager.getInstance()
        navigationManager.initialize(stage)

        // Show the stage
        stage.show()
    }
}

fun main() {
    Application.launch(MainApp::class.java)
}
