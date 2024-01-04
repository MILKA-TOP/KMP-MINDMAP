package ru.lipt.details.editable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import ru.lipt.details.editable.models.EditableDetailsScreenUi
import ru.lipt.details.editable.models.EditableTestResultUi
import ru.lipt.testing.common.navigation.TestingNavigationDestinations

@Composable
fun EditableDetailsContent(
    screenModel: EditableDetailsScreenModel,
) {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val uiState = screenModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Details") },
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

        screenModel.handleNavigation { target ->
            when (target) {
                NavigationTarget.SaveText -> scope.launch {
                    snackBarHostState.showSnackbar("Saved")
                }
                is NavigationTarget.EditTest -> navigator.push(
                    ScreenRegistry.get(
                        TestingNavigationDestinations.TestEditScreenDestination(target.params)
                    )
                )
            }
        }
        SnackbarHost(hostState = snackBarHostState)

        val ui = uiState.model

        Content(
            ui = ui,
            onValueChange = screenModel::onEditText,
            onTextSave = screenModel::onTextSaveButtonClick,
            onTestResultButtonClick = screenModel::onEditTestClick,
            scrollState = scrollState,
        )
    }
}

@Composable
private fun Content(
    ui: EditableDetailsScreenUi,
    scrollState: ScrollState = rememberScrollState(),
    onValueChange: (String) -> Unit = {},
    onTextSave: () -> Unit = {},
    onTestResultButtonClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier
                .sizeIn(maxWidth = 460.dp)
                .fillMaxSize()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = ui.text,
                onValueChange = onValueChange,
            )

            Button(onClick = onTextSave) {
                Text(text = "Save")
            }

            val onTestResultButtonText: String = remember(ui.testResult) {
                when (ui.testResult) {
                    is EditableTestResultUi.NoTest -> "Create test"
                    is EditableTestResultUi.EditTest -> "Edit test"
                }
            }

            Button(onClick = onTestResultButtonClick) {
                Text(text = onTestResultButtonText)
            }
        }
    }
}
