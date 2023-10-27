import androidx.compose.ui.window.ComposeUIViewController

actual fun getPlatformName(): String = "iOS"

@Suppress("FunctionNaming")
fun MainViewController() = ComposeUIViewController { App() }
