package ru.lipt.catalog.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

object CatalogScreen : Screen {

    @Composable
    override fun Content() {
        CatalogContent(
            screenModel = getScreenModel(),
        )
    }
}
