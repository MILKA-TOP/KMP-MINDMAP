package ru.lipt.catalog.create

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf
import ru.lipt.catalog.common.params.CreateMindMapParams

class CreateMindMapScreen(
    val params: CreateMindMapParams,
) : Screen {
    @Composable
    override fun Content() {
        CreateMindMapContent(
            screen = this,
            screenModel = getScreenModel(parameters = { parametersOf(params) }),
        )
    }
}
