package ru.lipt.login.registry.input

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class RegistryInputScreen : Screen {

    @Composable
    override fun Content() {
        RegistryInputContent(
            screen = this,
        )
    }
}
