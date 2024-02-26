package ru.lipt.login.common.components

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.core.compose.alert.AlertDialog
import ru.lipt.login.common.MR

@Composable
fun LogoutAlertDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = stringResource(MR.strings.logout_alert_title),
        text = stringResource(MR.strings.logout_alert_message),
        confirmText = stringResource(MR.strings.logout_alert_confirm),
        cancelText = stringResource(MR.strings.logout_alert_cancel),
        onCancel = onCancel,
        onConfirm = onConfirm,
    )
}
