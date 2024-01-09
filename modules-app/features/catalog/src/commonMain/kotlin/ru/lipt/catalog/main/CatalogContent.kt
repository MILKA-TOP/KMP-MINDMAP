package ru.lipt.catalog.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.catalog.main.models.CatalogScreenUi
import ru.lipt.catalog.main.models.MapCatalogElement
import ru.lipt.catalog.navigation.PrivateCatalogDestinations
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.login.common.navigation.LoginNavigationDestinations
import ru.lipt.map.common.navigation.MapNavigationDestinations

@Composable
fun CatalogContent(
    screenModel: CatalogScreenModel,
) {

    val navigator = LocalNavigator.currentOrThrow

    val uiState = screenModel.uiState.collectAsState().value

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.MapDestination -> navigator.push(
                ScreenRegistry.get(
                    MapNavigationDestinations.MapScreenDestination(target.params)
                )
            )
            NavigationTarget.HelloScreenDestination -> {
                navigator.replaceAll(
                    ScreenRegistry.get(
                        LoginNavigationDestinations.HelloScreenDestination
                    )
                )
            }
            NavigationTarget.CreateMindMapDestination -> {
                navigator.push(
                    ScreenRegistry.get(
                        PrivateCatalogDestinations.CreateMapDestination
                    )
                )
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
                title = { Text(text = "Catalog Screen") },
                actions = {
                    IconButton(onClick = screenModel::logout) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = ""
                        )
                    }
                    IconButton(onClick = screenModel::createNewMindMap) {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) {
        Content(
            ui = uiState.model,
            onMapElementClick = screenModel::onMapElementClick,
        )
    }
}

@Composable
private fun Content(
    ui: CatalogScreenUi,
    onMapElementClick: (String) -> Unit,
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ui.maps.forEach { map ->
            MapListElement(
                map = map,
                onMapElementClick = onMapElementClick,
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MapListElement(
    map: MapCatalogElement,
    onMapElementClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .sizeIn(maxWidth = 400.dp, maxHeight = 300.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colors.background)
            .clickable(onClick = { onMapElementClick(map.id) })
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
    }
}
