package ru.lipt.login.pin.input

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.catalog.common.navigation.CatalogNavigationDestinations
import ru.lipt.core.compose.alert.AlertDialog
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.coreui.components.PinField
import ru.lipt.coreui.components.ProgressButton
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.login.MR
import ru.lipt.login.common.navigation.LoginNavigationDestinations
import ru.lipt.login.pin.input.model.PinPadInputModel

@Composable
fun PinPadInputContent(
    screen: Screen,
    screenModel: PinPadInputScreenModel = screen.getScreenModel()
) {

    val scrollState = rememberScrollState()
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

    Scaffold {
        Content(
            ui = ui,
            scrollState = scrollState,
            onPinTextChanged = screenModel::onPinChanged,
            onSubmitPinButtonClick = screenModel::onSubmitPinButtonClick,
            onLogoutButtonClick = screenModel::onLogoutButtonClick,
            onConfirmLogOutAlertButtonClick = screenModel::onConfirmLogOutAlertButtonClick,
            onCloseLogOutAlert = screenModel::onCloseLogOutAlert,
        )
    }
}

@Composable
private fun Content(
    ui: PinPadInputModel,
    scrollState: ScrollState,
    onPinTextChanged: (String) -> Unit,
    onSubmitPinButtonClick: () -> Unit,
    onLogoutButtonClick: () -> Unit,
    onConfirmLogOutAlertButtonClick: () -> Unit,
    onCloseLogOutAlert: () -> Unit,
) {
    if (ui.showLogOutAlert) {
        LogoutAlertDialog(
            onCancel = onCloseLogOutAlert,
            onConfirm = onConfirmLogOutAlertButtonClick,
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .sizeIn(maxWidth = 360.dp)
                .align(Alignment.TopCenter)
                .padding(all = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(MR.strings.enter_pin_title),
                style = MindTheme.typography.material.h5
            )
            Spacer(modifier = Modifier.height(16.dp))
            PinField(
                title = stringResource(MR.strings.create_pin_field_enter),
                modifier = Modifier.fillMaxWidth(),
                onTextChanged = onPinTextChanged,
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row {
                ProgressButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(MR.strings.enter_pin_logout_button_title),
                    onClick = onLogoutButtonClick,
                    enabled = ui.isLogOutButtonEnabled,
                    inProgress = ui.isLogOutButtonInProgress,
                    colors = ButtonDefaults.outlinedButtonColors(),
                )

                Spacer(modifier = Modifier.width(32.dp))

                ProgressButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(MR.strings.enter_pin_enter_button_title),
                    onClick = onSubmitPinButtonClick,
                    enabled = ui.isSetPinButtonEnabled,
                    inProgress = ui.isSetButtonInProgress,
                )
            }
        }
    }
}

@Composable
private fun LogoutAlertDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        title = stringResource(MR.strings.enter_pin_logout_alert_title),
        text = stringResource(MR.strings.enter_pin_logout_alert_message),
        confirmText = stringResource(MR.strings.enter_pin_logout_alert_confirm),
        cancelText = stringResource(MR.strings.enter_pin_logout_alert_cancel),
        onCancel = onCancel,
        onConfirm = onConfirm,
    )
}
