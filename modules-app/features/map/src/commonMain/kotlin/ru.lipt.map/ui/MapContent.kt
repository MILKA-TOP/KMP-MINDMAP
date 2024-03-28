package ru.lipt.map.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.core.compose.alert.EnterAlertDialogV2
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.details.common.navigation.NodeDetailsNavigationDestinations
import ru.lipt.map.MR
import ru.lipt.map.PrivateMapNavigationDestinations
import ru.lipt.map.ui.common.MindMapComponent
import ru.lipt.map.ui.models.MapScreenUi

@Composable
fun MapContent(
    screen: Screen,
    screenModel: MapScreenModel,
) {
    screen.LifecycleEffect(
        onStarted = screenModel::onStarted
    )
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model

    val navigator = LocalNavigator.currentOrThrow

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.EditableDetailsScreen -> navigator.push(
                ScreenRegistry.get(
                    NodeDetailsNavigationDestinations.EditableNodeDetailsScreenDestination(target.params)
                )
            )
            is NavigationTarget.UneditableDetailsScreen -> navigator.push(
                ScreenRegistry.get(
                    NodeDetailsNavigationDestinations.UneditableNodeDetailsScreenDestination(target.params)
                )
            )
            is NavigationTarget.MapDetailsEditScreenDestination -> navigator.push(
                ScreenRegistry.get(
                    PrivateMapNavigationDestinations.MapEditDetails(target.params)
                )
            )
            is NavigationTarget.MapDetailsViewScreenDestination -> navigator.push(
                ScreenRegistry.get(
                    PrivateMapNavigationDestinations.MapViewDetails(target.params)
                )
            )
            is NavigationTarget.NavigateUp -> navigator.pop()
        }
    }

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.background, elevation = 0.dp, title = {
                val title = ui.data?.title ?: stringResource(MR.strings.map_screen_app_bar_title_placeholder)
                Text(
                    text = title, style = MindTheme.typography.material.h5
                )
            }, navigationIcon = {
                IconButton(onClick = screenModel::onBackButtonClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = ""
                    )
                }
            }, actions = {
                IconButton(onClick = screenModel::openMapDetails) {
                    Icon(
                        imageVector = Icons.Filled.List, contentDescription = ""
                    )
                }
                if (ui.data?.isSaveButtonVisible == true) {
                    IconButton(onClick = screenModel::saveMindMap) {
                        Icon(
                            imageVector = Icons.Filled.Done, contentDescription = ""
                        )
                    }
                }
            })
        }) {

            ui.onSuccess {
                MindMapScreen(
                    ui = data,
                    onCreateNewNode = screenModel::onCreateNewNode,
                    onFieldTextChanged = screenModel::onFieldTextChanged,
                    onConfirm = screenModel::onConfirm,
                    onCancel = screenModel::onCancel,
                    onOpenDetailsNode = screenModel::onEditNodeClick,
                    onViewNodeClick = screenModel::onViewNodeClick,
                )
            }

            ui.onLoading {
                CircularProgressIndicatorLoadingScreen()
            }

            ui.onError { ErrorScreen(onRefresh = screenModel::init) }
        }

        val updateInProgress = ui.data?.updateInProgress == true

        if (updateInProgress) {
            Box(
                Modifier.fillMaxSize().alpha(0.4f).clickable(onClick = {})
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun MindMapScreen(
    ui: MapScreenUi,
    onCreateNewNode: (String) -> Unit,
    onOpenDetailsNode: (String) -> Unit,
    onFieldTextChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onViewNodeClick: (String) -> Unit,
) {

    ui.alert?.let { alert ->
        PrivateAlertAddConfirm(
            ui = alert,
            onFieldTextChanged = onFieldTextChanged,
            onConfirm = onConfirm,
            onCancel = onCancel,
        )
    }

    MindMapComponent(
        ui = ui.box,
        onCreateNewNode = onCreateNewNode,
        onOpenDetailsNode = onOpenDetailsNode,
        onViewNodeClick = onViewNodeClick,
    )
}

@Composable
private fun PrivateAlertAddConfirm(
    ui: MapScreenUi.EnterNewNodeTitle,
    onFieldTextChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    EnterAlertDialogV2(
        text = stringResource(MR.strings.map_screen_enter_title_message),
        confirmText = stringResource(MR.strings.map_screen_enter_title_confirm_button),
        cancelText = stringResource(MR.strings.map_screen_enter_title_cancel_button),
        fieldLabel = stringResource(MR.strings.map_screen_enter_title_field_label),
        maxSymbols = 25,
        onConfirm = onConfirm,
        onCancel = onCancel,
        inProgress = ui.inProgress,
        confirmButtonEnabled = ui.isConfirmButtonEnabled,
        fieldTextValue = ui.title,
        onFieldTextChanged = onFieldTextChanged,
    )
}
