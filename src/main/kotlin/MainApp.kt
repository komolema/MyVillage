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
import database.DatabaseConfig
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
    }
    DatabaseConfig.initialize()
    application {
        val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
        Window(onCloseRequest = ::exitApplication, state = windowState) {
            App()
        }
    }
}
