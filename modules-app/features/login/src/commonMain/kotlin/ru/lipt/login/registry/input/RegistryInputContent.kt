package ru.lipt.login.registry.input

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.coreui.components.ProgressButton
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.login.MR
import ru.lipt.login.pin.PrivatePinPadDestinations
import ru.lipt.login.registry.input.model.RegistryInputModel

@Composable
fun RegistryInputContent(
    screen: Screen,
    navigator: Navigator = LocalNavigator.currentOrThrow,
    screenModel: RegistryInputScreenModel = screen.getScreenModel<RegistryInputScreenModel>()
) {
    val scrollState = rememberScrollState()
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
                backgroundColor = MaterialTheme.colors.background,
                title = { },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                elevation = 0.dp,
            )
        }
    ) {
        Content(
            ui = ui,
            scrollState = scrollState,
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
    scrollState: ScrollState,
    onEmailTextChanged: (String) -> Unit,
    onPasswordTextChanged: (String) -> Unit,
    onPasswordRepeatTextChanged: (String) -> Unit,
    onRegistryButtonClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .sizeIn(maxWidth = 360.dp)
                .align(Alignment.TopCenter)
                .padding(all = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(MR.strings.registry_screen_header),
                style = MindTheme.typography.material.h5
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = ui.email,
                onValueChange = onEmailTextChanged,
                label = { Text(stringResource(MR.strings.registry_field_title_email)) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = ui.password,
                onValueChange = onPasswordTextChanged,
                label = { Text(stringResource(MR.strings.registry_field_title_password)) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(MR.strings.registry_field_hint_password),
                style = MindTheme.typography.material.caption
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = ui.passwordRepeat,
                onValueChange = onPasswordRepeatTextChanged,
                label = { Text(stringResource(MR.strings.registry_field_title_repeat_password)) },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(MR.strings.registry_continue),
                enabled = ui.registryButtonEnable,
                inProgress = ui.buttonInProgress,
                onClick = onRegistryButtonClick
            )
        }
    }
}
