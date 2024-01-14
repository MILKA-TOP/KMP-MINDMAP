package ru.lipt.login.pin.create

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class PinPadCreateScreen : Screen {

    @Composable
    override fun Content() {
        PinPadCreateContent(screen = this)
    }
}
