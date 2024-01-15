package ru.lipt.map.details

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf
import ru.lipt.map.common.params.MapScreenParams

class MapDetailsScreen(
    private val params: MapScreenParams
) : Screen {

    @Composable
    override fun Content() {
        MapDetailsContent(
            screenModel = getScreenModel(parameters = { parametersOf(params) })
        )
    }
}
