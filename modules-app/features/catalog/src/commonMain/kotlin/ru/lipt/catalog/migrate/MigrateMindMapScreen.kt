package ru.lipt.catalog.migrate

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class MigrateMindMapScreen : Screen {
    @Composable
    override fun Content() {
        CreateMindMapContent(
            screen = this,
            screenModel = getScreenModel(),
        )
    }
}
