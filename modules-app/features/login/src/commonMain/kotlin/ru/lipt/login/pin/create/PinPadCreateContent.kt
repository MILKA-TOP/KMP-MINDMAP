package ru.lipt.login.pin.create

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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.coreui.components.PinField
import ru.lipt.coreui.components.ProgressButton
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.login.MR
import ru.lipt.login.pin.create.models.PinPadCreateModel

@Composable
fun PinPadCreateContent(
    screen: Screen,
    screenModel: PinPadCreateScreenModel = screen.getScreenModel()
) {

    val navigator = LocalNavigator.currentOrThrow
    val scrollState = rememberScrollState()

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
            onPinTextChanged = screenModel::onPinChanged,
            onPinRepeatTextChanged = screenModel::onPinRepeatChanged,
            onSetButtonClick = screenModel::submitPin
        )
    }
}

@Composable
private fun Content(
    ui: PinPadCreateModel,
    scrollState: ScrollState,
    onPinTextChanged: (String) -> Unit,
    onPinRepeatTextChanged: (String) -> Unit,
    onSetButtonClick: () -> Unit,
) {

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
                text = stringResource(MR.strings.create_pin_title),
                style = MindTheme.typography.material.h5
            )
            Spacer(modifier = Modifier.height(16.dp))
            PinField(
                title = stringResource(MR.strings.create_pin_field_enter),
                modifier = Modifier.fillMaxWidth(),
                onTextChanged = onPinTextChanged,
            )
            Spacer(modifier = Modifier.height(16.dp))
            PinField(
                title = stringResource(MR.strings.create_repeat_pin_field_enter),
                modifier = Modifier.fillMaxWidth(),
                onTextChanged = onPinRepeatTextChanged
            )
            Spacer(modifier = Modifier.height(32.dp))
            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(MR.strings.create_pin_button_title),
                onClick = onSetButtonClick,
                enabled = ui.isPinEnabled
            )
        }
    }
}
