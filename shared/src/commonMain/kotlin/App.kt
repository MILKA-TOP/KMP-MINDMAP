import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.login.splash.SplashScreen

@Composable
fun App() {
    MindTheme {
        Navigator(SplashScreen)
    }
}

expect fun getPlatformName(): String
