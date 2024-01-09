package ru.lipt.catalog.create

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object CreateMindMapScreen : Screen {
    @Composable
    override fun Content() {
        CreateMindMapContent(screen = this)
    }
}
