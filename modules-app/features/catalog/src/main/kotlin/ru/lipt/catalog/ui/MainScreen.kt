package ru.lipt.catalog.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class MainScreen: Screen {

    @Composable
    override fun Content() {
        MainContent()
    }
}