package ru.lipt.details.editable

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf
import ru.lipt.details.common.params.NodeDetailsScreenParams

class EditableDetailsScreen(
    val params: NodeDetailsScreenParams,
) : Screen {

    @Composable
    override fun Content() {
        EditableDetailsContent(
            screen = this,
            screenModel = getScreenModel(parameters = { parametersOf(params) })
        )
    }
}
