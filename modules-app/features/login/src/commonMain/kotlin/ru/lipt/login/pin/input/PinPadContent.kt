package ru.lipt.login.pin.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.catalog.common.navigation.CatalogNavigationDestinations
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.login.common.navigation.LoginNavigationDestinations
import ru.lipt.login.pin.input.model.PinPadInputModel

@Composable
fun PinPadInputContent(
    screen: Screen,
    screenModel: PinPadInputScreenModel = screen.getScreenModel()
) {

    val navigator = LocalNavigator.currentOrThrow

    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.CatalogScreenNavigate -> {
                navigator.replaceAll(ScreenRegistry.get(CatalogNavigationDestinations.CatalogScreenDestination))
            }
            is NavigationTarget.HelloScreenNavigate -> {
                navigator.replaceAll(ScreenRegistry.get(LoginNavigationDestinations.HelloScreenDestination))
            }
        }
    }

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "PinPad input") },
            )
        }
    ) {
        Content(
            ui = ui,
            onPinTextChanged = screenModel::onPinChanged,
            onSubmitPinButtonClick = screenModel::onSubmitPinButtonClick,
            onLogoutButtonClick = screenModel::onLogoutButtonClick,
        )
    }
}

@Composable
private fun Content(
    ui: PinPadInputModel,
    onPinTextChanged: (String) -> Unit,
    onSubmitPinButtonClick: () -> Unit,
    onLogoutButtonClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = ui.pin,
            onValueChange = onPinTextChanged,
            label = { Text("PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Button(onClick = onSubmitPinButtonClick, enabled = ui.isPinEnabled) {
            Text("Set pin")
        }
        Button(onClick = onLogoutButtonClick) {
            Text("LogOut")
        }
    }
}
