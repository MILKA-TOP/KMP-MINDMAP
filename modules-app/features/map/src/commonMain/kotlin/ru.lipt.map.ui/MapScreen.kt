package ru.lipt.map.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf
import ru.lipt.map.common.params.MapScreenParams

class MapScreen(
    private val params: MapScreenParams
) : Screen {

    @Composable
    override fun Content() {
        MapContent(
            screenModel = getScreenModel(parameters = { parametersOf(params) }),
        )
    }
}
