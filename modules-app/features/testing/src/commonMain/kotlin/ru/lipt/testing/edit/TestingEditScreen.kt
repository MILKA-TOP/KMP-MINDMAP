package ru.lipt.testing.edit

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf
import ru.lipt.testing.common.params.TestEditScreenParams

class TestingEditScreen(
    private val params: TestEditScreenParams
) : Screen {

    @Composable
    override fun Content() {
        TestingEditContent(
            screenModel = getScreenModel(parameters = { parametersOf(params) })
        )
    }
}
