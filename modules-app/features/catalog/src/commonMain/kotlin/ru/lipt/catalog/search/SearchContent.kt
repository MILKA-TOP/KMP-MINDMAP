package ru.lipt.catalog.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.catalog.search.models.MapQueryElement
import ru.lipt.catalog.search.models.SearchScreenUi
import ru.lipt.core.compose.alert.EnterAlertDialog
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onIdle
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.map.common.navigation.MapNavigationDestinations

@Composable
fun SearchContent(
    screen: Screen,
    screenModel: SearchScreenModel = screen.getScreenModel(),
) {
    val navigator = LocalNavigator.currentOrThrow

    val uiState = screenModel.uiState.collectAsState().value

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.ToMapNavigate -> navigator.replace(
                ScreenRegistry.get(MapNavigationDestinations.MapScreenDestination(target.params))
            )
        }
    }

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Catalog Screen") },
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
            ui = uiState.model,
            onSearchTextChanged = screenModel::onSearchTextChanged,
            onLoadButtonClick = screenModel::loadMaps,
            onMapElementClick = screenModel::onMapElementClick,
            closeEnterAlert = screenModel::onHidePasswordAlert,
            onEnterPassword = screenModel::onPasswordEnter,
        )
    }
}

@Composable
private fun Content(
    ui: SearchScreenUi,
    onSearchTextChanged: (String) -> Unit,
    onLoadButtonClick: () -> Unit,
    onMapElementClick: (String) -> Unit,
    onEnterPassword: (String) -> Unit,
    closeEnterAlert: () -> Unit,
) {

    if (ui.enterPasswordAlert != null) {
        EnterAlertDialog(
            title = "Password",
            text = "Please, enter the password of this MindMap",
            fieldLabel = "Password",
            confirmText = "Enter",
            cancelText = "Cancel",
            onConfirm = onEnterPassword,
            inProgress = ui.enterPasswordAlert.inProgress,
            onDismissRequest = closeEnterAlert,
            onCancel = closeEnterAlert,
        )
    }

    Column {
        TextField(
            value = ui.searchText,
            onValueChange = onSearchTextChanged,
            label = { Text("Search") },
        )
        ui.content.onIdle {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "введите текст для поиска или приватный id"
                )
            }
        }
        ui.content.onSuccess {
            data.maps.map {
                CustomMapListElement(
                    map = it,
                    onMapElementClick = onMapElementClick,
                )
            }
        }
        ui.content.onLoading { CircularProgressIndicatorLoadingScreen() }
        ui.content.onError { ErrorScreen(onRefresh = onLoadButtonClick) }
    }
}

@Composable
private fun CustomMapListElement(
    map: MapQueryElement,
    onMapElementClick: (String) -> Unit
) {
    Row {
        Column(
            modifier = Modifier
                .sizeIn(maxWidth = 400.dp, maxHeight = 300.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colors.background)
                .clickable(
                    enabled = map.enabled,
                    onClick = { onMapElementClick(map.id) }
                )
        ) {
            Text(
                text = map.title,
                style = MaterialTheme.typography.h5,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = map.description,
                style = MaterialTheme.typography.body1,
            )
            if (map.isNeedPassword) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "!!!Private!!!",
                    style = MaterialTheme.typography.caption,
                )
            }
        }
        if (map.isLoading) {
            CircularProgressIndicator()
        }
    }
}
