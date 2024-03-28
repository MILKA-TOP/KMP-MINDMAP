package ru.lipt.map.ui.view

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf
import ru.lipt.map.common.params.MapViewScreenParams

class MapViewScreen(
    private val params: MapViewScreenParams
) : Screen {

    @Composable
    override fun Content() {
        MapViewContent(
            screenModel = getScreenModel(parameters = { parametersOf(params) }),
        )
    }
}
