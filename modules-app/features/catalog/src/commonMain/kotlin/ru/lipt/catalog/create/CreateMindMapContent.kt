package ru.lipt.catalog.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
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
import ru.lipt.catalog.create.models.CreateMindMapModel
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.map.common.navigation.MapNavigationDestinations

@Composable
fun CreateMindMapContent(
    screen: Screen,
    screenModel: CreateMindMapScreenModel = screen.getScreenModel(),
) {
    val navigator = LocalNavigator.currentOrThrow

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
                title = { Text(text = "Create Mind-map") },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
            )
        }
    ) {
        Content(
            ui = uiState.model,
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
    onButtonClick: () -> Unit,
    onEnabledPasswordCheckboxClick: (Boolean) -> Unit,
    onDescriptionTextChanged: (String) -> Unit,
    onTitleTextChanged: (String) -> Unit,
    onPasswordTextChanged: (String) -> Unit,
) {
    Column {
        TextField(
            value = ui.title,
            onValueChange = onTitleTextChanged,
            label = { Text("Title") },
        )
        TextField(
            value = ui.description,
            onValueChange = onDescriptionTextChanged,
            label = { Text("Description") },
        )
        Row {
            TextField(
                value = ui.password,
                onValueChange = onPasswordTextChanged,
                label = { Text("Password") },
                enabled = ui.enabledPassword,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Checkbox(
                checked = ui.enabledPassword,
                onCheckedChange = onEnabledPasswordCheckboxClick,
            )
        }
        Button(onClick = onButtonClick, enabled = ui.createButtonEnabled) {
            Text("Create")
        }
    }
}
