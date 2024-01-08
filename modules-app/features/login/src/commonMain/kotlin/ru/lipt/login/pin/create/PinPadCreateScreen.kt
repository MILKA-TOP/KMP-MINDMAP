package ru.lipt.login.pin.create

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class PinPadCreateScreen : Screen {

    @Composable
    override fun Content() {
        PinPadCreateContent(screenModel = getScreenModel())
    }
}
