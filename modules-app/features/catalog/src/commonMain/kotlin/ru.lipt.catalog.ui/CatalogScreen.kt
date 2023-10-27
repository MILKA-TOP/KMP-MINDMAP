package ru.lipt.catalog.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object CatalogScreen : Screen {

    @Composable
    override fun Content() {
        CatalogContent()
    }
}
