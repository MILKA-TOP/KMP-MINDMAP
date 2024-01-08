package ru.lipt.login.registry.input

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object RegistryInputScreen : Screen {

    @Composable
    override fun Content() {
        RegistryInputContent(
            screen = this,
        )
    }
}
