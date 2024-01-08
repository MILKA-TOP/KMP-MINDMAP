package ru.lipt.login.hello

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HelloContent(
    screenModel: HelloScreenModel,
) {
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model

    screenModel.handleNavigation { _ ->
    }

    Content(
        onLoginButtonClick = screenModel::onLoginButtonClick,
        onRegistryButtonClick = screenModel::onRegistryButtonClick,
    )
}

@Composable
private fun Content(
    onLoginButtonClick: () -> Unit,
    onRegistryButtonClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center)) {
            Text("Mind-Map Education")
            Button(onClick = onLoginButtonClick) {
                Text("Login")
            }
            Button(onClick = onRegistryButtonClick) {
                Text("Registry")
            }
        }
    }
}
