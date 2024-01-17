package ru.lipt.testing.result

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf
import ru.lipt.testing.common.params.TestingResultParams

class TestingResultScreen(
    private val params: TestingResultParams
) : Screen {

    @Composable
    override fun Content() {
        TestingResultContent(
            screenModel = getScreenModel(parameters = { parametersOf(params) })
        )
    }
}
