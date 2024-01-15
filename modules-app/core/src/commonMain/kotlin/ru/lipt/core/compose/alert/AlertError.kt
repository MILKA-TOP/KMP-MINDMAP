package ru.lipt.core.compose.alert

import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorAlertDialog(
    error: UiError.Alert?,
    onDismissRequest: () -> Unit = { },
) {
    if (error != null) {
        AlertDialog(
            title = error.title,
            text = error.message,
            confirmText = error.confirmText,
            onConfirm = onDismissRequest,
            onCancel = onDismissRequest,
        )
    }
}

@Composable
fun AlertDialog(
    title: String? = null,
    text: String? = null,
    confirmText: String? = null,
    onConfirm: (() -> Unit)? = null,
    cancelText: String? = null,
    onCancel: (() -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = onCancel,
    inProgress: Boolean = false,
) {
    val buttonsTopPadding = remember(text) {
        if (text?.takeIf(String::isNotEmpty) == null) {
            28.dp
        } else {
            0.dp
        }
    }
    AlertDialog(
        onDismissRequest = { onDismissRequest?.invoke() },
        buttons = {
            AlertButtons(
                modifier = Modifier.padding(
                    start = 8.dp, end = 8.dp, top = buttonsTopPadding,
                ),
                confirmText = confirmText,
                onConfirm = onConfirm,
                cancelText = cancelText,
                onCancel = onCancel,
                enabledConfirmButton = !inProgress,
                inProgressConfirmation = inProgress,
            )
        },
        title = title?.takeIf(String::isNotEmpty)?.let {
            {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h4,
                )
            }
        },
        text = text?.takeIf(String::isNotEmpty)?.let {
            {
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    )
}
