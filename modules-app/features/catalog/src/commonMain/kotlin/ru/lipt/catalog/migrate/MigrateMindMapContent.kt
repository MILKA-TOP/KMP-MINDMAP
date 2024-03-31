package ru.lipt.catalog.migrate

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModelStore
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.MPFile
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.catalog.MR
import ru.lipt.catalog.common.navigation.CatalogNavigationDestinations
import ru.lipt.catalog.migrate.models.MigrateMindMapModel
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.coreui.components.ProgressButton
import ru.lipt.coreui.shapes.RoundedCornerShape16
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.map.common.navigation.MapNavigationDestinations

@Composable
fun CreateMindMapContent(
    screen: Screen,
    screenModel: MigrateMindMapScreenModel = screen.getScreenModel(),
) {
    val navigator = LocalNavigator.currentOrThrow
    val scrollState = rememberScrollState()

    val uiState = screenModel.uiState.collectAsState().value

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.MindMapScreen -> {
                val catalogScreen = ScreenRegistry.get(CatalogNavigationDestinations.CatalogScreenDestination)
                navigator.popUntil { popScreen ->
                    popScreen::class == catalogScreen::class.also {
                        ScreenModelStore.onDispose(popScreen)
                    }
                }
                navigator.push(ScreenRegistry.get(MapNavigationDestinations.MapScreenDestination(target.params)))
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
                        text = stringResource(MR.strings.migrate_map_title),
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
            onPasswordTextChanged = screenModel::onPasswordTextChanged,
            onFilePickerClick = screenModel::onFilePickerClick,
            onFileSelected = screenModel::onFileSelected

        )
    }
}

@Composable
private fun Content(
    ui: MigrateMindMapModel,
    scrollState: ScrollState,
    onButtonClick: () -> Unit,
    onEnabledPasswordCheckboxClick: (Boolean) -> Unit,
    onPasswordTextChanged: (String) -> Unit,
    onFileSelected: (MPFile<Any>?) -> Unit,
    onFilePickerClick: () -> Unit,
) {

    FilePicker(
        show = ui.showFilePicker,
        title = "Some title check",
        onFileSelected = onFileSelected
    )

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
                text = "Select file with Mind-Map from \"Mindomo\" service (text format)",
                style = MindTheme.typography.material.body1
            )

            Spacer(modifier = Modifier.height(16.dp))

            SelectFileField(
                selectedFileTitle = ui.selectedFileTitle,
                onFilePickerClick = onFilePickerClick,
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

@Composable
private fun SelectFileField(
    selectedFileTitle: String?,
    onFilePickerClick: () -> Unit,
) {
    val isFileSelected = selectedFileTitle != null
    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp).clip(RoundedCornerShape16).clickable(onClick = onFilePickerClick),
        backgroundColor = if (isFileSelected) MaterialTheme.colors.surface
        else MaterialTheme.colors.secondary
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(all = 4.dp).padding(start = 16.dp),
        ) {
            selectedFileTitle?.let { title ->
                Text(text = "Selected file:", style = MaterialTheme.typography.caption)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = title, style = MaterialTheme.typography.body1)
            } ?: Text(text = "Select file", style = MaterialTheme.typography.h6)
        }
    }
}
