package ru.lipt.details

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class DetailsScreen : Screen {

    @Composable
    override fun Content() {
        DetailsContent(
            screenModel = getScreenModel()
        )
    }
}
