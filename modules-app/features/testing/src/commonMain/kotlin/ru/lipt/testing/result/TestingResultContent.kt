package ru.lipt.testing.result

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.testing.result.models.TestingResultUi

@Composable
fun TestingResultContent(
    screenModel: TestingResultScreenModel,
) {
    val navigator = LocalNavigator.currentOrThrow
    val scrollState = rememberScrollState()
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Test result") },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) {

        ui.onLoading { CircularProgressIndicatorLoadingScreen() }

        ui.onSuccess {
            Content(
                ui = data,
                scrollState = scrollState,
            )
        }
    }
}

@Composable
private fun Content(
    ui: TestingResultUi,
    scrollState: ScrollState = rememberScrollState()
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        ui.questions.map { question ->
            QuestionResultComponent(question)
        }
    }
}
