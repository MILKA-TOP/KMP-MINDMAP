package ru.lipt.map.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class MapScreen : Screen {

    @Composable
    override fun Content() {
        MapContent(
            screenModel = getScreenModel(),
        )
    }
}
