package ru.lipt.core.compose.error

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
//    errorImage: Painter = painterResource(ru.bspb.core.ui.R.drawable.ic_error_placeholder),
    message: String? = null,
    refreshText: String? = null,
    onRefresh: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(Alignment.CenterVertically)
            .then(modifier)
    ) {
//        Image(
//            painter = errorImage,
//            contentDescription = "Error",
//            alignment = Alignment.Center,
//            modifier = Modifier
//                .align(alignment = Alignment.CenterHorizontally)
//        )

        Spacer(modifier = Modifier.height(32.dp))

        TextBlockActionItem(
            infoText = message ?: "Что-то пошло не так",
            actionButton = refreshText ?: "Повторить",
            onAction = onRefresh,
        )
    }
}
