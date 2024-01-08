package ru.lipt.login.hello

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class HelloScreen : Screen {

    @Composable
    override fun Content() {
        HelloContent(
            screenModel = getScreenModel()
        )
    }
}
