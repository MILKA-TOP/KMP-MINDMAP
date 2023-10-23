package ui.lipt.map.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class MapScreen: Screen {

    @Composable
    override fun Content() {
        MapContent()
    }
}