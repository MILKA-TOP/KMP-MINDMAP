package ru.lipt.testing.edit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import ru.lipt.core.compose.alert.AlertDialog
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
import ru.lipt.testing.edit.models.TestingEditScreenUi
import ru.lipt.testing.edit.question.QuestionEditComponent

@OptIn(ExperimentalFoundationApi::class)
@Suppress("UnusedPrivateMember")
@Composable
fun TestingEditContent(
    screenModel: TestingEditScreenModel,
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
                screenModel = screenModel,
                scrollState = scrollState,
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
    screenModel: TestingEditScreenModel, // TODO: Remove it
    ui: TestingEditScreenUi,
    scrollState: ScrollState,
) {
    val coroutineScope = rememberCoroutineScope()
    val horizontalPagerState = rememberPagerState { ui.questions.size }
    val snackBarHostState = remember { SnackbarHostState() }
    SnackbarHost(hostState = snackBarHostState)

    screenModel.handleNavigation { target ->
        when (target) {
            NavigationTarget.SuccessQuestionsSave -> {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar("Saved")
                }
            }
            is NavigationTarget.OpenQuestions -> {
                coroutineScope.launch {
                    horizontalPagerState.animateScrollToPage(target.position)
                }
            }
        }
    }
    when (ui.alert) {
        TestingEditScreenUi.Alert.Remove -> RemoveAlert(
            onCancel = screenModel::onCloseAlert,
            onConfirm = screenModel::onRemoveQuestion,
        )
        TestingEditScreenUi.Alert.Generate -> GenerateAlert(
            onCancel = screenModel::onCloseAlert,
            onConfirm = screenModel::onGenerateConfirm,
        )
        null -> Unit
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.sizeIn(maxWidth = 360.dp).align(Alignment.TopCenter).padding(all = 16.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(ui.nodeTitle)
            Spacer(modifier = Modifier.height(16.dp))

            val lazyRowState = rememberLazyListState()
            HorizontalPager(
                modifier = Modifier.fillMaxWidth(), state = horizontalPagerState, userScrollEnabled = false
            ) { position ->
                QuestionEditComponent(
                    model = ui.questions[position],
                    onHeaderTextChanged = { screenModel.onHeaderTextChanged(position, it) },
                    onSingleCheckboxSelect = { screenModel.onSingleSelectChanged(position, it) },
                    onMultipleCheckboxSelect = { pos, bool -> screenModel.onMultipleSelectChanged(position, pos, bool) },
                    onNewItemClick = { screenModel.onNewItemAdd(position) },
                    onCloseClick = { screenModel.onCloseClick(position) },
                    onFieldTextChanged = { pos, text -> screenModel.onItemTextChanged(position, pos, text) },
                    updateFieldType = { type -> screenModel.updateFieldType(position, type) },
                    onFieldRemoveClick = { screenModel.onRemoveAnswer(position, it) }
                )
            }
            NumberRow(
                list = ui.questions,
                horizontalPagerState = horizontalPagerState,
                lazyRowState = lazyRowState,
                onIndicatorPageClick = screenModel::onIndicatorPageClick,
                addQuestion = screenModel::addQuestion
            )
            Spacer(modifier = Modifier.height(32.dp))
            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(MR.strings.testing_edit_save_button),
                enabled = ui.isSaveButtonEnabled,
                onClick = screenModel::onSaveButtonClick
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(MR.strings.generate_questions_button_text),
                enabled = ui.isGenerateButtonEnabled,
                onClick = screenModel::onGenerateButtonClick,
                colors = ButtonDefaults.outlinedButtonColors()
            )
        }
        if (ui.isGenerateInProgress) {
            CircularProgressIndicatorLoadingScreen(modifier = Modifier.fillMaxSize().alpha(0.3f).clickable(onClick = {}))
        }
    }
}

@Composable
private fun Header(
    nodeTitle: String
) {
    Text(modifier = Modifier.fillMaxWidth(), style = MindTheme.typography.material.h5, text = buildAnnotatedString {
        append(stringResource(MR.strings.testing_edit_screen_title))
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.Bold,
            )
        ) {
            append("\"$nodeTitle\"")
        }
    })
}

@Composable
private fun RemoveAlert(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = stringResource(MR.strings.testing_edit_alert_remove_title),
        text = stringResource(MR.strings.testing_edit_alert_remove_message),
        confirmText = stringResource(MR.strings.testing_edit_alert_remove_remove),
        cancelText = stringResource(MR.strings.testing_edit_alert_remove_cancel),
        onCancel = onCancel,
        onConfirm = onConfirm,
    )
}

@Composable
private fun GenerateAlert(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = stringResource(MR.strings.testing_edit_alert_generate_title),
        text = stringResource(MR.strings.testing_edit_alert_generate_message),
        confirmText = stringResource(MR.strings.testing_edit_alert_generate_remove),
        cancelText = stringResource(MR.strings.testing_edit_alert_generate_cancel),
        onCancel = onCancel,
        onConfirm = onConfirm,
    )
}
