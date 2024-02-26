package ru.lipt.catalog.create

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
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
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.catalog.MR
import ru.lipt.catalog.create.models.CreateMindMapModel
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.coreui.components.OutlinedCountedTextField
import ru.lipt.coreui.components.ProgressButton
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.map.common.navigation.MapNavigationDestinations

@Composable
fun CreateMindMapContent(
    screen: Screen,
    screenModel: CreateMindMapScreenModel = screen.getScreenModel(),
) {
    val navigator = LocalNavigator.currentOrThrow
    val scrollState = rememberScrollState()

    val uiState = screenModel.uiState.collectAsState().value

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.MindMapScreen -> {
                navigator.replace(ScreenRegistry.get(MapNavigationDestinations.MapScreenDestination(target.params)))
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
                title = {
                    Text(
                        text = stringResource(MR.strings.create_map_title),
                        style = MindTheme.typography.material.h5
                    )
                },
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
            ui = uiState.model,
            scrollState = scrollState,
            onButtonClick = screenModel::onButtonClick,
            onEnabledPasswordCheckboxClick = screenModel::onEnabledPasswordCheckboxClick,
            onDescriptionTextChanged = screenModel::onDescriptionTextChanged,
            onTitleTextChanged = screenModel::onTitleTextChanged,
            onPasswordTextChanged = screenModel::onPasswordTextChanged,
        )
    }
}

@Composable
private fun Content(
    ui: CreateMindMapModel,
    scrollState: ScrollState,
    onButtonClick: () -> Unit,
    onEnabledPasswordCheckboxClick: (Boolean) -> Unit,
    onDescriptionTextChanged: (String) -> Unit,
    onTitleTextChanged: (String) -> Unit,
    onPasswordTextChanged: (String) -> Unit,
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
            ui.referralParams?.let { referralParams ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    Text(
                        modifier = Modifier.padding(all = 16.dp),
                        text = stringResource(MR.strings.create_map_copy_caption, referralParams),
                        style = MindTheme.typography.material.caption,
                        color = MaterialTheme.colors.onSurface,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            OutlinedCountedTextField(
                maxSymbols = 25,
                modifier = Modifier.fillMaxWidth(),
                value = ui.title,
                onValueChange = onTitleTextChanged,
                label = { Text(stringResource(MR.strings.create_map_field_title)) },
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedCountedTextField(
                maxSymbols = 200,
                modifier = Modifier.fillMaxWidth(),
                value = ui.description,
                onValueChange = onDescriptionTextChanged,
                label = { Text(stringResource(MR.strings.create_map_field_description)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = ui.password,
                    modifier = Modifier.weight(1f),
                    onValueChange = onPasswordTextChanged,
                    enabled = ui.enabledPassword,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    label = { Text(stringResource(MR.strings.create_map_field_password)) },
                    maxLines = 1,
                )
                Checkbox(
                    checked = ui.enabledPassword,
                    onCheckedChange = onEnabledPasswordCheckboxClick,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(MR.strings.create_map_field_password_size),
                style = MindTheme.typography.material.caption
            )
            Spacer(modifier = Modifier.height(32.dp))
            ProgressButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(MR.strings.create_map_field_create_button),
                enabled = ui.createButtonEnabled,
                inProgress = ui.buttonInProgress,
                onClick = onButtonClick
            )
        }
    }
}
