package ru.lipt.testing.complete

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf
import ru.lipt.testing.common.params.TestCompleteScreenParams

class TestingCompleteScreen(
    private val params: TestCompleteScreenParams
) : Screen {

    @Composable
    override fun Content() {
        TestingCompleteContent(
            screenModel = getScreenModel(parameters = { parametersOf(params) })
        )
    }
}
