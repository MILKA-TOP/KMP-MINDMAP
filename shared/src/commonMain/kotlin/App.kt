import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ru.lipt.catalog.ui.CatalogScreen

@Composable
fun App() {
    MaterialTheme {
        Navigator(CatalogScreen)
    }
}

expect fun getPlatformName(): String
