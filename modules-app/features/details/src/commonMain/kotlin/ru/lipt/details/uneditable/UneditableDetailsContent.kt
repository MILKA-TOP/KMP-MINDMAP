package ru.lipt.details.uneditable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
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
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.details.uneditable.models.UneditableDetailsScreenUi
import ru.lipt.details.uneditable.models.UneditableTestResultUi
import ru.lipt.testing.common.navigation.TestingNavigationDestinations

@Composable
fun UneditableDetailsContent(
    screen: Screen,
    screenModel: UneditableDetailsScreenModel
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Details") },
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
        ui.onSuccess {
            Content(
                ui = data,
                scrollState = scrollState,
                onTestNavigateClick = screenModel::onTestNavigateClick,
                onTestResultButtonClick = screenModel::onTestResultButtonClick,
            )
        }

        ui.onError { ErrorScreen(onRefresh = screenModel::init) }

        ui.onLoading { CircularProgressIndicatorLoadingScreen() }
    }
}

@Composable
private fun Content(
    ui: UneditableDetailsScreenUi,
    scrollState: ScrollState,
    onTestNavigateClick: () -> Unit,
    onTestResultButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        Text(
            text = ui.text,
        )
        when (ui.testResult) {
            UneditableTestResultUi.NoTest -> Unit
            UneditableTestResultUi.CompleteTest -> CompleteTestComponent(onTestNavigateClick)
            is UneditableTestResultUi.Result -> ResultTestComponent(
                ui = ui.testResult,
                onResultNavigateClick = onTestResultButtonClick
            )
        }
    }
}

@Composable
private fun CompleteTestComponent(
    onTestNavigateClick: () -> Unit,
) {
    Button(onClick = onTestNavigateClick) {
        Text("Пройти тест")
    }
}

@Composable
private fun ResultTestComponent(
    ui: UneditableTestResultUi.Result,
    onResultNavigateClick: () -> Unit,
) {
    Column {
        Text(ui.resultLine)
        ui.message?.let { Text(it) }
        Button(onClick = onResultNavigateClick) {
            Text("Посмотреть ответы")
        }
    }
}
