package ru.lipt.testing.result

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.testing.MR
import ru.lipt.testing.result.models.TestingResultUi

@Composable
fun TestingResultContent(
    screenModel: TestingResultScreenModel,
) {
    val navigator = LocalNavigator.currentOrThrow
    val scrollState = rememberScrollState()
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model

    Scaffold(topBar = {
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            title = {},
            navigationIcon = {
                IconButton(onClick = navigator::pop) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = ""
                    )
                }
            },
            elevation = 0.dp,
        )
    }) {

        ui.onSuccess {
            Content(
                ui = data,
                scrollState = scrollState,
            )
        }

        ui.onLoading {
            CircularProgressIndicatorLoadingScreen()
        }
    }
}

@Composable
private fun Content(
    ui: TestingResultUi,
    scrollState: ScrollState = rememberScrollState()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center).sizeIn(maxWidth = 360.dp).padding(all = 16.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(MR.strings.test_result_header),
                style = MindTheme.typography.material.h3,
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(32.dp))
            ui.questions.map { question ->
                QuestionResultComponent(question)
            }
        }
    }
}
