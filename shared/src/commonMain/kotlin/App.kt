import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ru.lipt.login.hello.HelloScreen

@Composable
fun App() {
    MaterialTheme {
        Navigator(HelloScreen())
    }
}

expect fun getPlatformName(): String
