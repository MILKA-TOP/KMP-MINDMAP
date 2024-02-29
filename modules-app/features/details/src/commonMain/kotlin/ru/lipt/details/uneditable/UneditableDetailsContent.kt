package ru.lipt.details.uneditable

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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.coreui.components.ProgressButton
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.coreui.typography.MindTypography
import ru.lipt.details.MR
import ru.lipt.details.uneditable.models.UneditableDetailsScreenUi
import ru.lipt.details.uneditable.models.UneditableTestResultUi
import ru.lipt.testing.common.navigation.TestingNavigationDestinations

@Composable
fun UneditableDetailsContent(
    screen: Screen, screenModel: UneditableDetailsScreenModel
) {
    screen.LifecycleEffect(
        onStarted = screenModel::onStarted
    )
    val navigator = LocalNavigator.currentOrThrow
    val scrollState = rememberScrollState()

    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.CompleteTest -> navigator.push(
                ScreenRegistry.get(
                    TestingNavigationDestinations.TestCompleteScreenDestination(
                        target.params
                    )
                )
            )
            is NavigationTarget.TestResult -> navigator.push(
                ScreenRegistry.get(
                    TestingNavigationDestinations.TestResultScreenDestination(
                        target.params
                    )
                )
            )
        }
    }

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
                onTestNavigateClick = screenModel::onTestNavigateClick,
                onTestResultButtonClick = screenModel::onTestResultButtonClick,
                onMarkButtonClick = screenModel::onMarkButtonClick,
            )
        }

        ui.onError { ErrorScreen(onRefresh = screenModel::init) }

        ui.onLoading { CircularProgressIndicatorLoadingScreen() }
    }
}

@Composable
private fun Content(
    ui: UneditableDetailsScreenUi, scrollState: ScrollState,
    onTestNavigateClick: () -> Unit,
    onTestResultButtonClick: () -> Unit,
    onMarkButtonClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.sizeIn(maxWidth = 360.dp).align(Alignment.TopCenter).padding(all = 16.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            val uriHandler = LocalUriHandler.current

            Text(
                modifier = Modifier.fillMaxWidth(), text = ui.title, style = MindTheme.typography.material.h4
            )
            Spacer(modifier = Modifier.height(16.dp))

            val text = ui.descriptionAnnotatedString
            ClickableText(modifier = Modifier.fillMaxWidth(),
                text = text,
                style = MindTypography.Material.body1.copy(textAlign = TextAlign.Justify),
                onClick = {
                    text.getStringAnnotations("URL", it, it).firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                    }
                })
            Spacer(modifier = Modifier.height(16.dp))
            when (val testResult = ui.testResult) {
                is UneditableTestResultUi.Result -> {
                    ResultTestComponent(
                        ui = testResult,
                        onResultNavigateClick = onTestResultButtonClick,
                    )
                }
                is UneditableTestResultUi.CompleteTest -> {
                    CompleteTestComponent(
                        onTestNavigateClick = onTestNavigateClick,
                    )
                }
                is UneditableTestResultUi.NoTest -> Unit
            }

            Spacer(modifier = Modifier.height(32.dp))
            ProgressButton(
                modifier = Modifier.fillMaxWidth(), text = ui.buttonText, colors = ui.buttonColor,
                inProgress = ui.isButtonInProgress,
                onClick = onMarkButtonClick,
            )
        }
    }
}

@Composable
private fun CompleteTestComponent(
    onTestNavigateClick: () -> Unit,
) {
    Button(
        modifier = Modifier.fillMaxWidth(), onClick = onTestNavigateClick
    ) {
        Text(stringResource(MR.strings.node_text_complete_test))
    }
}

@Composable
private fun ResultTestComponent(
    ui: UneditableTestResultUi.Result,
    onResultNavigateClick: () -> Unit,
) {
    Column {
        ui.message?.let { Text(it) }
        Spacer(modifier = Modifier.height(4.dp))
        Button(onClick = onResultNavigateClick) {
            Text(stringResource(MR.strings.node_text_see_results))
        }
    }
}
