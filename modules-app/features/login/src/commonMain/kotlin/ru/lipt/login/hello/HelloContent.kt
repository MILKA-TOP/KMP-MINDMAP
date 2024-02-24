package ru.lipt.login.hello

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.login.MR
import ru.lipt.login.navigation.PrivateLoginDestinations

@Composable
fun HelloContent(
    screenModel: HelloScreenModel,
) {
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model
    val navigator = LocalNavigator.currentOrThrow
    val scrollState = rememberScrollState()

    screenModel.handleNavigation { target ->
        when (target) {
            NavigationTarget.RegistryNavigate -> navigator.push(ScreenRegistry.get(PrivateLoginDestinations.RegistryInputDestination))
            NavigationTarget.LoginNavigate -> navigator.push(ScreenRegistry.get(PrivateLoginDestinations.LoginDestination))
        }
    }

    Content(
        onLoginButtonClick = screenModel::onLoginButtonClick,
        onRegistryButtonClick = screenModel::onRegistryButtonClick,
        scrollState = scrollState,
    )
}

@Composable
private fun Content(
    onLoginButtonClick: () -> Unit,
    onRegistryButtonClick: () -> Unit,
    scrollState: ScrollState = rememberScrollState(),
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .sizeIn(maxWidth = 360.dp)
                .padding(all = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(MR.strings.welcome_title),
                style = MindTheme.typography.material.h2,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(MR.strings.welcome_description),
                style = MindTheme.typography.material.body2,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onLoginButtonClick
            ) {
                Text(
                    text = stringResource(MR.strings.welcome_sign_up_text_button),
                    style = MindTheme.typography.material.button,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(12.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onRegistryButtonClick,
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Text(
                    text = stringResource(MR.strings.welcome_login_text_button),
                    style = MindTheme.typography.material.button,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
