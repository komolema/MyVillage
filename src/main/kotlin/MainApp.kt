import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.rememberNavController
import di.appModule
import di.daoModule
import org.koin.core.context.startKoin
import ui.navigation.AppNavigation

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    MaterialTheme {
        AppNavigation()
    }
}

fun main() = {
    startKoin{
        modules(appModule)
    }
    application {
        Window(onCloseRequest = ::exitApplication) {
            App()
        }
    }
}
