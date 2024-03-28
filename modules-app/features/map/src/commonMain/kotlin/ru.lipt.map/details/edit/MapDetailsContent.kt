package ru.lipt.map.details.edit

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.core.compose.OutlinedCountedTextField
import ru.lipt.core.compose.alert.AlertDialog
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.coreui.components.ProgressButton
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.map.MR
import ru.lipt.map.common.navigation.MapNavigationDestinations
import ru.lipt.map.details.edit.models.MapDetailsEditUi
import ru.lipt.map.details.edit.models.UserUi

@Composable
fun MapDetailsEditContent(
    screenModel: MapDetailsEditScreenModel,
) {
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model
    val scrollState = rememberScrollState()

    val navigator = LocalNavigator.currentOrThrow

    screenModel.handleNavigation { target ->
        when (target) {
            NavigationTarget.CatalogDestination -> navigator.popUntilRoot()
            is NavigationTarget.OpenUserMap -> {
                navigator.push(ScreenRegistry.get(MapNavigationDestinations.MapViewScreenDestination(target.params)))
            }
            NavigationTarget.PopBack -> navigator.pop()
        }
    }

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                title = {
                    Text(
                        text = stringResource(MR.strings.map_screen_details_title_appbar)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                elevation = 0.dp,
            )
        }
    ) {

        ui.onSuccess {
            Content(
                ui = data,
                scrollState = scrollState,
                onUserClick = screenModel::onUserClick,
                deleteMapClick = screenModel::onDeleteButtonClick,
                hideAlerts = screenModel::hideDialog,
                onUserAlertConfirm = screenModel::onUserAlertConfirm,
                cancelDeletingMap = screenModel::cancelDeletingMap,
                deleteMapAlertConfirm = screenModel::deleteMapAlertConfirm,
                onTitleTextChanged = screenModel::onTitleTextChanged,
                onDescriptionTextChanged = screenModel::onDescriptionTextChanged,
                onSaveClick = screenModel::onSaveClick,
            )
        }

        ui.onLoading {
            CircularProgressIndicatorLoadingScreen()
        }

        ui.onError { ErrorScreen(onRefresh = screenModel::init) }
    }
}

@Suppress("LongMethod")
@Composable
private fun Content(
    ui: MapDetailsEditUi,
    scrollState: ScrollState,
    onUserClick: (String) -> Unit,
    onTitleTextChanged: (String) -> Unit,
    onDescriptionTextChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    deleteMapClick: () -> Unit,
    hideAlerts: () -> Unit,
    cancelDeletingMap: () -> Unit,
    deleteMapAlertConfirm: () -> Unit,
    onUserAlertConfirm: (String) -> Unit,
) {
    ui.dialog?.let { dialog ->
        when (dialog) {
            is MapDetailsEditUi.Dialog.UserMap -> UserMapAlert(
                alert = dialog,
                onCancel = hideAlerts,
                onConfirm = onUserAlertConfirm,
            )
            is MapDetailsEditUi.Dialog.DeleteMap -> DeleteMapAlert(
                alert = dialog,
                onCancel = cancelDeletingMap,
                onConfirm = deleteMapAlertConfirm,
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .sizeIn(maxWidth = 360.dp)
                .align(Alignment.TopCenter)
                .padding(all = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCountedTextField(
                maxSymbols = 25,
                modifier = Modifier.fillMaxWidth(),
                value = ui.title,
                onValueChange = onTitleTextChanged,
                label = { Text(stringResource(MR.strings.map_screen_details_title_label)) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedCountedTextField(
                maxSymbols = 200,
                minLines = 4,
                modifier = Modifier.fillMaxWidth(),
                value = ui.description,
                onValueChange = onDescriptionTextChanged,
                label = { Text(stringResource(MR.strings.map_screen_details_description_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(MR.strings.map_screen_details_information),
                style = MindTheme.typography.material.caption
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(MR.strings.map_screen_details_admin, ui.admin),
                style = MindTheme.typography.material.body1
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(all = 16.dp)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(MR.strings.map_screen_details_referral_id),
                        style = MindTheme.typography.material.caption
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = ui.inviteUid,
                        style = MindTheme.typography.material.body1
                    )
                }
            }

            if (ui.users.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(MR.strings.map_screen_details_saved_by_title),
                    style = MindTheme.typography.material.caption
                )
                Spacer(modifier = Modifier.height(8.dp))
                UsersSection(
                    accessUsers = ui.users,
                    onUserClick = onUserClick,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(MR.strings.map_screen_details_actions),
                style = MindTheme.typography.material.caption
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.clickable(onClick = deleteMapClick),
                backgroundColor = MindTheme.colors.unmarkedNode
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp)) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(MR.strings.map_screen_details_remove_from_all)
                    )
                    Icon(
                        modifier = Modifier.padding(start = 16.dp),
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(MR.strings.map_screen_details_save),
                enabled = ui.buttonIsEnabled,
                onClick = onSaveClick
            )
        }
    }
}

@Composable
private fun UsersSection(
    accessUsers: List<UserUi>,
    onUserClick: (String) -> Unit,
) {
    var showMore by remember { mutableStateOf(false) }

    // Creating a clickable modifier
    // that consists text

    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Column(modifier = Modifier.clickable(onClick = { showMore = !showMore })) {
                Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp)) {
                    Icon(
                        modifier = Modifier.padding(end = 16.dp),
                        imageVector = Icons.Filled.Person,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(MR.strings.map_screen_details_users_count, accessUsers.size.toString())
                    )
                    Icon(
                        modifier = Modifier.padding(start = 16.dp),
                        imageVector = if (showMore) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                        contentDescription = null
                    )
                }
            }
            Column(
                modifier = Modifier
                    .animateContentSize(animationSpec = tween(100))
            ) {

                if (showMore) {
                    accessUsers.map {
                        Divider()
                        Text(
                            modifier = Modifier.fillMaxWidth().clickable(onClick = { onUserClick(it.id) }).padding(all = 16.dp),
                            text = it.email,
                            style = MaterialTheme.typography.body2,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserMapAlert(
    alert: MapDetailsEditUi.Dialog.UserMap,
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    AlertDialog(
        title = stringResource(MR.strings.map_screen_alert_user_title),
        text = stringResource(MR.strings.map_screen_alert_user_message, alert.email),
        confirmText = stringResource(MR.strings.map_screen_alert_user_ok),
        cancelText = stringResource(MR.strings.map_screen_alert_user_cancel),
        onCancel = onCancel,
        onConfirm = { onConfirm(alert.userId) }
    )
}

@Composable
private fun DeleteMapAlert(
    alert: MapDetailsEditUi.Dialog.DeleteMap,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = stringResource(MR.strings.map_screen_alert_remove_title),
        text = stringResource(MR.strings.map_screen_alert_remove_message),
        confirmText = stringResource(MR.strings.map_screen_alert_remove_remove),
        cancelText = stringResource(MR.strings.map_screen_alert_remove_cancel),
        onCancel = onCancel,
        onConfirm = onConfirm,
        inProgress = alert.inProgress,
    )
}
