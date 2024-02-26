package ru.lipt.core.compose.alert

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun EnterAlertDialogV2(
    title: String? = null,
    text: String? = null,
    fieldLabel: String? = null,
    confirmText: String? = null,
    inProgress: Boolean = false,
    onConfirm: () -> Unit = { },
    confirmButtonEnabled: Boolean = true,
    fieldTextValue: String = "",
    onFieldTextChanged: (String) -> Unit = {},
    cancelText: String? = null,
    onCancel: (() -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = onCancel,
) {
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
            Card {
                Column(
                    modifier = Modifier.padding(all = 16.dp)
                ) {

                    Text(
                        text = title.orEmpty(),
                        style = MaterialTheme.typography.h4,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = text.orEmpty(),
                        style = MaterialTheme.typography.body1,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        enabled = !inProgress,
                        label = { Text(fieldLabel.orEmpty()) },
                        value = fieldTextValue,
                        onValueChange = onFieldTextChanged
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    AlertButtons(
                        modifier = Modifier.padding(
                            start = 8.dp, end = 8.dp, top = buttonsTopPadding,
                        ),
                        confirmText = confirmText,
                        onConfirm = onConfirm,
                        cancelText = cancelText,
                        onCancel = onCancel,
                        enabledConfirmButton = !inProgress && confirmButtonEnabled,
                        inProgressConfirmation = inProgress
                    )
                }
            }
        }
    )
}
