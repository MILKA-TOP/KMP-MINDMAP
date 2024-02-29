package ru.lipt.details.editable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import ru.lipt.core.compose.OutlinedCountedTextField
import ru.lipt.core.compose.alert.AlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.coreui.components.ProgressButton
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.details.MR
import ru.lipt.details.editable.models.EditableDetailsScreenUi
import ru.lipt.details.editable.models.EditableTestResultUi
import ru.lipt.details.editable.models.RemoveAlertUi
import ru.lipt.testing.common.navigation.TestingNavigationDestinations

@Composable
fun EditableDetailsContent(
    screen: Screen,
    screenModel: EditableDetailsScreenModel,
) {
    screen.LifecycleEffect(
        onStarted = screenModel::onStarted
    )
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val uiState = screenModel.uiState.collectAsState().value

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

        val savedDataButton = stringResource(MR.strings.node_details_snackbar_saved)

        screenModel.handleNavigation { target ->
            when (target) {
                is NavigationTarget.EditTest -> navigator.push(
                    ScreenRegistry.get(
                        TestingNavigationDestinations.TestEditScreenDestination(target.params)
                    )
                )
                NavigationTarget.SuccessSave -> scope.launch {
                    snackBarHostState.showSnackbar(savedDataButton)
                }
                NavigationTarget.SuccessRemove -> navigator.pop()
            }
        }

        val ui = uiState.model

        ui.onSuccess {
            Content(
                ui = data,
                snackBarHostState = snackBarHostState,
                onDescriptionChange = screenModel::onEditDescriptionText,
                onTestEditButtonClick = screenModel::onTestEditButtonClick,
                onTitleTextChanged = screenModel::onEditTitleText,
                onSaveButtonClick = screenModel::onSaveButtonClick,
                onRemoveButtonClick = screenModel::onRemoveButtonClick,
                onConfirmRemoveNode = screenModel::onRemoveAlertConfirm,
                onCancelRemoveNode = screenModel::onRemoveAlertClose,
                scrollState = scrollState,
            )
        }

        ui.onLoading {
            CircularProgressIndicatorLoadingScreen()
        }

        ui.onError { ErrorScreen(onRefresh = screenModel::init) }
    }
}

@Composable
private fun Content(
    ui: EditableDetailsScreenUi,
    snackBarHostState: SnackbarHostState,
    scrollState: ScrollState = rememberScrollState(),
    onTitleTextChanged: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTestEditButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    onRemoveButtonClick: () -> Unit,
    onConfirmRemoveNode: () -> Unit,
    onCancelRemoveNode: () -> Unit,
) {
    SnackbarHost(hostState = snackBarHostState)

    ui.remoevAlertUi?.let {
        RemoveNodeAlertDialog(
            ui = it,
            onCancel = onCancelRemoveNode,
            onConfirm = onConfirmRemoveNode,
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.sizeIn(maxWidth = 360.dp).align(Alignment.TopCenter).padding(all = 16.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(MR.strings.node_details_appbar_title), style = MindTheme.typography.material.h5
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedCountedTextField(
                enabled = !ui.isRootNode,
                maxSymbols = 25,
                modifier = Modifier.fillMaxWidth(),
                value = ui.title,
                onValueChange = onTitleTextChanged,
                label = { Text(stringResource(MR.strings.node_details_appbar_field_nodes_title)) },
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = ui.description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(MR.strings.node_details_appbar_field_nodes_description)) },
                minLines = 4,
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProgressButton(
                onClick = onTestEditButtonClick, modifier = Modifier.fillMaxWidth(), text = when (ui.testResult) {
                    is EditableTestResultUi.NoTest -> stringResource(MR.strings.node_details_create_test)
                    is EditableTestResultUi.EditTest -> stringResource(MR.strings.node_details_edit_test)
                }, colors = ButtonDefaults.outlinedButtonColors()
            )

            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProgressButton(
                    onClick = onRemoveButtonClick,
                    modifier = Modifier.weight(1f),
                    enabled = !ui.isRootNode,
                    text = stringResource(MR.strings.node_details_remove_button),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.error)
                )
                ProgressButton(
                    onClick = onSaveButtonClick,
                    modifier = Modifier.weight(1f),
                    enabled = ui.isSaveButtonEnabled,
                    text = stringResource(MR.strings.node_details_save_button),
                )
            }
        }
    }
}

@Composable
private fun RemoveNodeAlertDialog(
    ui: RemoveAlertUi,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = stringResource(MR.strings.remove_node_alert_title),
        text = stringResource(MR.strings.remove_node_alert_message, ui.parentTitle),
        confirmText = stringResource(MR.strings.remove_node_alert_confirm),
        cancelText = stringResource(MR.strings.remove_node_alert_cancel),
        onCancel = onCancel,
        onConfirm = onConfirm,
    )
}
