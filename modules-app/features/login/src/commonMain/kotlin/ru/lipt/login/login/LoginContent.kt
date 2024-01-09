package ru.lipt.login.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.login.login.models.LoginUiModel
import ru.lipt.login.pin.PrivatePinPadDestinations

@Composable
fun LoginContent(
    screen: Screen,
    screenModel: LoginScreenModel = screen.getScreenModel()
) {
    val navigator = LocalNavigator.currentOrThrow
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    screenModel.handleNavigation { target ->
        when (target) {
            NavigationTarget.PinCreateNavigate -> {
                navigator.replace(ScreenRegistry.get(PrivatePinPadDestinations.CreatePin))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Login Input") },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) {
        Content(
            ui = ui,
            onEmailTextChanged = screenModel::onEmailTextChanged,
            onPasswordTextChanged = screenModel::onPasswordTextChanged,
            onLoginButtonClick = screenModel::onLoginButtonClick
        )
    }
}

@Composable
fun Content(
    ui: LoginUiModel,
    onEmailTextChanged: (String) -> Unit,
    onPasswordTextChanged: (String) -> Unit,
    onLoginButtonClick: () -> Unit,
) {

    Column {
        TextField(
            value = ui.email,
            onValueChange = onEmailTextChanged,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        TextField(
            value = ui.password,
            onValueChange = onPasswordTextChanged,
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(onClick = onLoginButtonClick, enabled = ui.loginButtonEnable) {
            Text("Login")
        }
    }
}
