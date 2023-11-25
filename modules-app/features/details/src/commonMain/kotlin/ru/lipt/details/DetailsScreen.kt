package ru.lipt.details

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import ru.lipt.details.common.params.NodeDetailsScreenParams

class DetailsScreen(
    val params: NodeDetailsScreenParams,
) : Screen {

    @Composable
    override fun Content() {
        DetailsContent(
            screenModel = getScreenModel()
        )
    }
}
