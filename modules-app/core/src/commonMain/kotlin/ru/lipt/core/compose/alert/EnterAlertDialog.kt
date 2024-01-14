package ru.lipt.core.compose.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun EnterAlertDialog(
    title: String? = null,
    text: String? = null,
    fieldLabel: String? = null,
    confirmText: String? = null,
    inProgress: Boolean = false,
    onConfirm: ((String) -> Unit) = { },
    cancelText: String? = null,
    onCancel: (() -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = onCancel,
) {
    var enterText by remember { mutableStateOf("") }
    val buttonsTopPadding = remember(text) {
        if (text?.takeIf(String::isNotEmpty) == null) {
            28.dp
        } else {
            0.dp
        }
    }
    Dialog(
        onDismissRequest = { onDismissRequest?.invoke() },
        content = {
            Column(
                modifier = Modifier.background(Color.White)
            ) {
                Text(
                    text = title.orEmpty(),
                    style = MaterialTheme.typography.h4,
                )
                Text(
                    text = text.orEmpty(),
                    style = MaterialTheme.typography.body1,
                )

                TextField(
                    enabled = !inProgress,
                    label = { Text(fieldLabel.orEmpty()) },
                    value = enterText,
                    onValueChange = { enterText = it }
                )
                if (inProgress) LinearProgressIndicator()

                AlertButtons(
                    modifier = Modifier.padding(
                        start = 8.dp, end = 8.dp, top = buttonsTopPadding,
                    ),
                    confirmText = confirmText,
                    onConfirm = { onConfirm(enterText) },
                    cancelText = cancelText,
                    onCancel = onCancel,
                    enabledConfirmButton = !inProgress,
                )
            }
        }
    )
}
