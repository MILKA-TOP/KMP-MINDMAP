package ru.lipt.login.registry.input

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
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.login.pin.PrivatePinPadDestinations
import ru.lipt.login.registry.input.model.RegistryInputModel

@Composable
fun RegistryInputContent(
    screen: Screen,
    navigator: Navigator = LocalNavigator.currentOrThrow,
    screenModel: RegistryInputScreenModel = screen.getScreenModel<RegistryInputScreenModel>()
) {

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
                title = { Text(text = "Map Editor") },
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
            onPasswordRepeatTextChanged = screenModel::onPasswordRepeatTextChanged,
            onRegistryButtonClick = screenModel::onRegistryButtonClick
        )
    }
}

@Composable
fun Content(
    ui: RegistryInputModel,
    onEmailTextChanged: (String) -> Unit,
    onPasswordTextChanged: (String) -> Unit,
    onPasswordRepeatTextChanged: (String) -> Unit,
    onRegistryButtonClick: () -> Unit,
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
        TextField(
            value = ui.passwordRepeat,
            onValueChange = onPasswordRepeatTextChanged,
            label = { Text("Repeat password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(onClick = onRegistryButtonClick, enabled = ui.registryButtonEnable) {
            Text("Registry")
        }
    }
}
