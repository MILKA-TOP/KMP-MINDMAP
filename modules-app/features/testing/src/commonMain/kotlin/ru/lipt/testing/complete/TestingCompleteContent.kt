package ru.lipt.testing.complete

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.coreui.components.ProgressButton
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.testing.MR
import ru.lipt.testing.common.NumberRow
import ru.lipt.testing.common.navigation.TestingNavigationDestinations
import ru.lipt.testing.common.params.TestingResultParams
import ru.lipt.testing.complete.models.TestingCompleteScreenUi

@Composable
fun TestingCompleteContent(
    screenModel: TestingCompleteScreenModel
) {
    val navigator = LocalNavigator.currentOrThrow
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model
    val scrollState = rememberScrollState()

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

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
                screenModel = screenModel,
                onResultNavigate = { params ->
                    navigator.replace(
                        ScreenRegistry.get(
                            TestingNavigationDestinations.TestResultScreenDestination(params)
                        )
                    )
                }
            )
        }

        ui.onLoading {
            CircularProgressIndicatorLoadingScreen()
        }

        ui.onError { ErrorScreen(onRefresh = screenModel::init) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    ui: TestingCompleteScreenUi,
    scrollState: ScrollState,
    screenModel: TestingCompleteScreenModel,
    onResultNavigate: (TestingResultParams) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val horizontalPagerState = rememberPagerState { ui.questions.size }
    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.Result -> onResultNavigate(target.params)
            is NavigationTarget.OpenQuestions -> coroutineScope.launch {
                horizontalPagerState.animateScrollToPage(target.position)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.sizeIn(maxWidth = 360.dp).align(Alignment.TopCenter).padding(all = 16.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(MR.strings.testing_header, ui.nodeTitle),
                style = MindTheme.typography.material.h3,
            )
            Spacer(modifier = Modifier.height(32.dp))

            HorizontalPager(
                modifier = Modifier.fillMaxWidth(), state = horizontalPagerState, userScrollEnabled = false
            ) { position ->
                QuestionCompleteComponent(
                    model = ui.questions[position],
                    onSingleCheckboxSelect = { screenModel.onSingleSelectChanged(position, it) },
                    onMultipleCheckboxSelect = { pos, bool -> screenModel.onMultipleSelectChanged(position, pos, bool) },
                )
            }
            val lazyRowState = rememberLazyListState()
            NumberRow(
                list = ui.questions,
                horizontalPagerState = horizontalPagerState,
                lazyRowState = lazyRowState,
                onIndicatorPageClick = screenModel::onIndicatorPageClick,
                enabledShowAddItem = false
            )
            Spacer(modifier = Modifier.height(32.dp))
            ProgressButton(
                text = stringResource(MR.strings.testing_complete_button),
                modifier = Modifier.fillMaxWidth(),
                inProgress = ui.buttonInProgress,
                enabled = ui.isAnswerButtonEnabled,
                onClick = screenModel::onSaveButtonClick
            )
        }
    }
}
