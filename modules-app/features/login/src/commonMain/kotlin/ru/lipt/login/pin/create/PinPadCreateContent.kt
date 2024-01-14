package ru.lipt.login.pin.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.catalog.common.navigation.CatalogNavigationDestinations
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.login.pin.create.models.PinPadCreateModel

@Composable
fun PinPadCreateContent(
    screen: Screen,
    screenModel: PinPadCreateScreenModel = screen.getScreenModel()
) {

    val navigator = LocalNavigator.currentOrThrow

    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.CatalogNavigate -> {
                navigator.replaceAll(ScreenRegistry.get(CatalogNavigationDestinations.CatalogScreenDestination))
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
                title = { Text(text = "PinPad create") },
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
            onPinTextChanged = screenModel::onPinChanged,
            onPinRepeatTextChanged = screenModel::onPinRepeatChanged,
            onSetButtonClick = screenModel::submitPin
        )
    }
}

@Composable
private fun Content(
    ui: PinPadCreateModel,
    onPinTextChanged: (String) -> Unit,
    onPinRepeatTextChanged: (String) -> Unit,
    onSetButtonClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = ui.pin,
            onValueChange = onPinTextChanged,
            label = { Text("PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        TextField(
            value = ui.pinRepeat,
            onValueChange = onPinRepeatTextChanged,
            label = { Text("Repeat PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Button(onClick = onSetButtonClick, enabled = ui.isPinEnabled) {
            Text("Set pin")
        }
    }
}
