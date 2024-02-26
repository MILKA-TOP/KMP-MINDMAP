package ru.lipt.catalog.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.catalog.MR
import ru.lipt.catalog.main.MapListElement
import ru.lipt.catalog.search.models.SearchAlerts
import ru.lipt.catalog.search.models.SearchScreenUi
import ru.lipt.core.compose.alert.AlertDialog
import ru.lipt.core.compose.alert.EnterAlertDialogV2
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onIdle
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.map.common.navigation.MapNavigationDestinations

@Composable
fun SearchContent(
    screen: Screen,
    screenModel: SearchScreenModel = screen.getScreenModel(),
) {
    val navigator = LocalNavigator.currentOrThrow
    val scrollState = rememberLazyListState()

    val uiState = screenModel.uiState.collectAsState().value

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.ToMapNavigate -> navigator.replace(
                ScreenRegistry.get(MapNavigationDestinations.MapScreenDestination(target.params))
            )
        }
    }

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    Scaffold(topBar = {
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            title = {
                Text(
                    text = stringResource(MR.strings.catalog_screen_title),
                    style = MindTheme.typography.material.h5
                )
            },
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
        Content(
            ui = uiState.model,
            scrollState = scrollState,
            onSearchTextChanged = screenModel::onSearchTextChanged,
            onLoadButtonClick = screenModel::loadMaps,
            onMapElementClick = screenModel::onMapElementClick,
            onConfirmAddPublicMapAlert = screenModel::onConfirmAddPublicMapAlert,
            onConfirmAddPrivateMapAlert = screenModel::onConfirmAddPrivateMapAlert,
            onHideAlert = screenModel::onHideAddAlert,
            onEnterPassword = screenModel::onPasswordEnter,
        )
    }
}

@Composable
private fun Content(
    ui: SearchScreenUi,
    scrollState: LazyListState,
    onSearchTextChanged: (String) -> Unit,
    onLoadButtonClick: () -> Unit,
    onMapElementClick: (String) -> Unit,
    onConfirmAddPublicMapAlert: () -> Unit,
    onConfirmAddPrivateMapAlert: () -> Unit,
    onHideAlert: () -> Unit,
    onEnterPassword: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    when (val uiAlert = ui.addMindMapAlert) {
        is SearchAlerts.PublicMap -> PublicAlertAddConfirm(
            ui = uiAlert,
            onConfirm = onConfirmAddPublicMapAlert,
            onCancel = onHideAlert,
        )
        is SearchAlerts.PrivateMap -> PrivateAlertAddConfirm(
            ui = uiAlert,
            onConfirm = onConfirmAddPrivateMapAlert,
            onCancel = onHideAlert,
            onFieldTextChanged = onEnterPassword,
        )
        null -> Unit
    }

//    if (ui.enterPasswordAlert != null) {
//        EnterAlertDialog(
//            title = "Password",
//            text = "Please, enter the password of this MindMap",
//            fieldLabel = "Password",
//            confirmText = "Enter",
//            cancelText = "Cancel",
//            onConfirm = onEnterPassword,
//            inProgress = ui.enterPasswordAlert.inProgress,
//            onDismissRequest = closeEnterAlert,
//            onCancel = closeEnterAlert,
//        )
//    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .sizeIn(maxWidth = 360.dp)
                .fillMaxHeight()
                .align(Alignment.TopCenter)
                .padding(all = 16.dp)
                .focusRequester(focusRequester),
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    value = ui.searchText,
                    onValueChange = onSearchTextChanged,
                    label = { Text(stringResource(MR.strings.catalog_screen_field_search_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search, contentDescription = ""
                        )
                    })
                Spacer(Modifier.height(12.dp))
            }

            ui.content.onIdle {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(MR.strings.search_map_idle_caption_text),
                            style = MindTheme.typography.material.body2,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            ui.content.onLoading { item { CircularProgressIndicatorLoadingScreen() } }
            ui.content.onError { item { ErrorScreen(onRefresh = onLoadButtonClick) } }
            ui.content.onSuccess {
                if (data.maps.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = stringResource(MR.strings.search_map_success_empty_caption_text),
                                style = MindTheme.typography.material.body2,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    items(data.maps) { map ->
                        MapListElement(
                            map = map,
                            onMapElementClick = onMapElementClick,
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PublicAlertAddConfirm(
    ui: SearchAlerts.PublicMap,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        title = stringResource(MR.strings.search_map_alert_title_add),
        text = stringResource(MR.strings.search_map_alert_message_add, ui.title),
        confirmText = stringResource(MR.strings.search_map_alert_confirm_button),
        cancelText = stringResource(MR.strings.search_map_alert_cancel_button),
        onConfirm = onConfirm,
        onCancel = onCancel,
        inProgress = ui.inProgress,
    )
}

@Composable
private fun PrivateAlertAddConfirm(
    ui: SearchAlerts.PrivateMap,
    onFieldTextChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    EnterAlertDialogV2(
        title = stringResource(MR.strings.search_map_alert_title_add),
        text = stringResource(MR.strings.search_map_alert_message_add, ui.title),
        confirmText = stringResource(MR.strings.search_map_alert_confirm_button),
        cancelText = stringResource(MR.strings.search_map_alert_cancel_button),
        fieldLabel = stringResource(MR.strings.search_map_alert_password_field),
        onConfirm = onConfirm,
        onCancel = onCancel,
        inProgress = ui.inProgress,
        confirmButtonEnabled = ui.isConfirmButtonEnabled,
        fieldTextValue = ui.password,
        onFieldTextChanged = onFieldTextChanged,
    )
}
