package ru.lipt.coreui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressButton(
    text: String,
    modifier: Modifier = Modifier,
    inProgress: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) = Button(
    modifier = Modifier.sizeIn(minHeight = 48.dp).then(modifier),
    onClick = onClick,
    enabled = enabled
) {
    val localContentColor = LocalContentColor.current
    if (inProgress) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 3.dp,
            color = localContentColor
        )
    } else {
        Text(
            text = text,
        )
    }
}
