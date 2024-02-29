package ru.lipt.map.details.view

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.catalog.common.navigation.CatalogNavigationDestinations
import ru.lipt.core.compose.alert.AlertDialog
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.coreui.shapes.RoundedCornerShape12
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.map.MR
import ru.lipt.map.details.view.models.MapDetailsViewUi

@Composable
fun MapDetailsViewContent(
    screenModel: MapDetailsViewScreenModel,
) {
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model
    val scrollState = rememberScrollState()

    val navigator = LocalNavigator.currentOrThrow

    screenModel.handleNavigation { target ->
        when (target) {
            NavigationTarget.CatalogDestination -> navigator.popUntilRoot()
            is NavigationTarget.CopyMap -> navigator.push(
                ScreenRegistry.get(
                    CatalogNavigationDestinations.CreateMapDestination(target.params)
                )
            )
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
                hideMapClick = screenModel::onHideButtonClick,
                removeMapClick = screenModel::onRemoveMapClick,
                cancelHidingMap = screenModel::cancelDeletingMap,
                hideMapAlertConfirm = screenModel::hideMapAlertConfirm,
                clearProgressMapAlertConfirm = screenModel::clearProgressMapAlertConfirm,
                copyMapClick = screenModel::copyMapClick,
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
    ui: MapDetailsViewUi,
    scrollState: ScrollState,
    hideMapClick: () -> Unit,
    removeMapClick: () -> Unit,
    copyMapClick: () -> Unit,
    cancelHidingMap: () -> Unit,
    hideMapAlertConfirm: () -> Unit,
    clearProgressMapAlertConfirm: () -> Unit,
) {
    ui.dialog?.let { dialog ->
        when (dialog) {
            is MapDetailsViewUi.Dialog.HideMap -> HideMapAlert(
                alert = dialog,
                onCancel = cancelHidingMap,
                onConfirm = hideMapAlertConfirm,
            )
            is MapDetailsViewUi.Dialog.RemoveMap -> ClearProgressMapAlert(
                alert = dialog,
                onCancel = cancelHidingMap,
                onConfirm = clearProgressMapAlertConfirm,
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(all = 16.dp)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(MR.strings.map_screen_details_title_label),
                        style = MindTheme.typography.material.caption
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = ui.title,
                        style = MindTheme.typography.material.body1
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (ui.description.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(all = 16.dp)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(MR.strings.map_screen_details_description_label),
                            style = MindTheme.typography.material.caption
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = ui.description,
                            style = MindTheme.typography.material.body1
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

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

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(MR.strings.map_screen_details_actions),
                style = MindTheme.typography.material.caption
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.clip(RoundedCornerShape12).clickable(onClick = hideMapClick),
                backgroundColor = MindTheme.colors.unmarkedNode
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp)) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(MR.strings.map_screen_details_hide)
                    )
                    Icon(
                        modifier = Modifier.padding(start = 16.dp),
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.clip(RoundedCornerShape12).clickable(onClick = removeMapClick),
                backgroundColor = MindTheme.colors.unmarkedNode
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp)) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(MR.strings.map_screen_details_delete_clear),
                        color = MindTheme.colors.material.error
                    )
                    Icon(
                        modifier = Modifier.padding(start = 16.dp),
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.clip(RoundedCornerShape12).clickable(onClick = copyMapClick),
                backgroundColor = MindTheme.colors.material.surface
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(all = 16.dp)) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(MR.strings.map_screen_copy_map_title)
                    )
                    Icon(
                        modifier = Modifier.padding(start = 16.dp),
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun HideMapAlert(
    alert: MapDetailsViewUi.Dialog.HideMap,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = stringResource(MR.strings.map_screen_alert_hide_title),
        text = stringResource(MR.strings.map_screen_alert_hide_message),
        confirmText = stringResource(MR.strings.map_screen_alert_hide_remove),
        cancelText = stringResource(MR.strings.map_screen_alert_hide_cancel),
        onCancel = onCancel,
        onConfirm = onConfirm,
        inProgress = alert.inProgress,
    )
}

@Composable
private fun ClearProgressMapAlert(
    alert: MapDetailsViewUi.Dialog.RemoveMap,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = stringResource(MR.strings.map_screen_alert_delete_clear_title),
        text = stringResource(MR.strings.map_screen_alert_delete_clear_message),
        confirmText = stringResource(MR.strings.map_screen_alert_delete_clear_remove),
        cancelText = stringResource(MR.strings.map_screen_alert_delete_clear_cancel),
        onCancel = onCancel,
        onConfirm = onConfirm,
        inProgress = alert.inProgress,
    )
}
