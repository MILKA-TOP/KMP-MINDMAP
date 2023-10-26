import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import ru.lipt.shared.di.initKoin

lateinit var koin: Koin

fun main() = application {
    if (GlobalContext.getOrNull() == null) {
        initKoin { }
    }
    val koin = GlobalContext.get()
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}