package ru.lipt.catalog.search

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object SearchScreen : Screen {

    @Composable
    override fun Content() {
        SearchContent(screen = this)
    }
}
