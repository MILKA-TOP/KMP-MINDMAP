import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.login.splash.SplashScreen

@Composable
fun App() {
    MindTheme {
        Navigator(SplashScreen) { navigator ->
            SlideTransition(navigator)
        }
    }
}

expect fun getPlatformName(): String
