package ru.lipt.login.pin.input

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class PinPadInputScreen : Screen {
    @Composable
    override fun Content() {
        PinPadInputContent(screen = this)
    }
}
