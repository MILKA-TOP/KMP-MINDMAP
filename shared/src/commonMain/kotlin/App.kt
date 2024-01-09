import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ru.lipt.login.splash.SplashScreen

@Composable
fun App() {
    MaterialTheme {
        Navigator(SplashScreen)
    }
}

expect fun getPlatformName(): String
