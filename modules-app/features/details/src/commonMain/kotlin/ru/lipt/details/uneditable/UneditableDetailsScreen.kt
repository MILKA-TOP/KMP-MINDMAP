package ru.lipt.details.uneditable

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf
import ru.lipt.details.common.params.NodeDetailsScreenParams

class UneditableDetailsScreen(
    private val params: NodeDetailsScreenParams
) : Screen {

    @Composable
    override fun Content() {
        UneditableDetailsContent(
            screenModel = getScreenModel(parameters = { parametersOf(params) })
        )
    }
}
