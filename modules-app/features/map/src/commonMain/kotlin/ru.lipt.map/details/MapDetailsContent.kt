package ru.lipt.map.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.core.compose.alert.AlertDialog
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.map.details.models.MapDetailsUi

@Composable
fun MapDetailsContent(
    screenModel: MapDetailsScreenModel,
) {
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model

    val navigator = LocalNavigator.currentOrThrow

    screenModel.handleNavigation { target ->
        when (target) {
            NavigationTarget.CatalogDestination -> navigator.popUntilRoot()
            is NavigationTarget.OpenUserMap -> Unit
            is NavigationTarget.CopyMap -> Unit
        }
    }

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Map Details") },
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
                onUserClick = screenModel::onUserClick,
                copyMapClick = screenModel::copyMapClick,
                deleteMapClick = screenModel::onDeleteButtonClick,
                hideAlerts = screenModel::hideDialog,
                onUserAlertConfirm = screenModel::onUserAlertConfirm,
                cancelDeletingMap = screenModel::cancelDeletingMap,
                deleteMapAlertConfirm = screenModel::deleteMapAlertConfirm,
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
    ui: MapDetailsUi,
    onUserClick: (String) -> Unit,
    copyMapClick: () -> Unit,
    deleteMapClick: () -> Unit,
    hideAlerts: () -> Unit,
    cancelDeletingMap: () -> Unit,
    deleteMapAlertConfirm: () -> Unit,
    onUserAlertConfirm: (String) -> Unit,
) {
    ui.dialog?.let { dialog ->
        when (dialog) {
            is MapDetailsUi.Dialog.UserMap -> UserMapAlert(
                alert = dialog,
                onCancel = hideAlerts,
                onConfirm = onUserAlertConfirm,
            )
            is MapDetailsUi.Dialog.DeleteMap -> DeleteMapAlert(
                alert = dialog,
                onCancel = cancelDeletingMap,
                onConfirm = deleteMapAlertConfirm,
            )
        }
    }
    Column {
        Text("Title: ${ui.title}")
        Text("Description: ${ui.description}")
        Text("InviteId: ${ui.inviteUid}")
        Text("Admin: ${ui.admin}")
        if (ui.users.isNotEmpty()) {
            Column {
                Text("Users")
                ui.users.map {
                    Text(it.email, modifier = Modifier.clickable { onUserClick(it.id) })
                }
            }
        }

        Button(onClick = copyMapClick) {
            Text("Copy map")
        }

        if (ui.enabledShowDeleteMap) {
            Button(onClick = deleteMapClick) {
                Text("Delete map")
            }
        }
    }
}

@Composable
private fun UserMapAlert(
    alert: MapDetailsUi.Dialog.UserMap,
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    AlertDialog(
        title = "Прогресс пользователя",
        text = "Посмотреть прогресс пользователя ${alert.email}",
        confirmText = "Ok",
        cancelText = "Отменить",
        onCancel = onCancel,
        onConfirm = { onConfirm(alert.userId) }
    )
}

@Composable
private fun DeleteMapAlert(
    alert: MapDetailsUi.Dialog.DeleteMap,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = "Удалить",
        text = "Вы дейстивительно хотите удалить данную диаграмму? В случае удаления все данные и прогресс будет утерян",
        confirmText = "Удалить",
        cancelText = "Отменить",
        onCancel = onCancel,
        onConfirm = onConfirm,
        inProgress = alert.inProgress,
    )
}
