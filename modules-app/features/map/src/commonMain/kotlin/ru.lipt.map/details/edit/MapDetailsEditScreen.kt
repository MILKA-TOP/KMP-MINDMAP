package ru.lipt.map.details.edit

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf
import ru.lipt.map.common.params.MapScreenParams

class MapDetailsEditScreen(
    private val params: MapScreenParams
) : Screen {

    @Composable
    override fun Content() {
        MapDetailsEditContent(
            screenModel = getScreenModel(parameters = { parametersOf(params) })
        )
    }
}
