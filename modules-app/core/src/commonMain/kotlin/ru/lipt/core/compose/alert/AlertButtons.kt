package ru.lipt.core.compose.alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AlertButtons(
    modifier: Modifier = Modifier,
    cancelText: String? = null,
    onCancel: (() -> Unit)? = null,
    confirmText: String? = null,
    onConfirm: (() -> Unit)? = null,
    enabledConfirmButton: Boolean = true,
    inProgressConfirmation: Boolean = false,
) = FlowRow(
    modifier = modifier
        .fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(
        space = 12.dp,
        alignment = Alignment.End,
    ),
) {
    if (cancelText != null && onCancel != null) {
        TextButton(
            onClick = onCancel,
            content = {
                Text(
                    text = cancelText.uppercase(),
                    style = MaterialTheme.typography.button,
                )
            }
        )
    }
    if (confirmText != null && onConfirm != null) {
        if (inProgressConfirmation) {
            Button(onClick = onConfirm, enabled = enabledConfirmButton) {
                CircularProgressIndicator()
            }
        } else {
            TextButton(
                onClick = onConfirm,
                enabled = enabledConfirmButton,
                content = {
                    Text(
                        text = confirmText.uppercase(),
                        style = MaterialTheme.typography.button,
                    )
                }
            )
        }
    }
}
