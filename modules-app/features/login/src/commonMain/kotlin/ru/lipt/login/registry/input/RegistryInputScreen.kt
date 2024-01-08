package ru.lipt.login.registry.input

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

object RegistryInputScreen : Screen {

    @Composable
    override fun Content() {
        RegistryInputContent(
            screenModel = getScreenModel(),
        )
    }
}
