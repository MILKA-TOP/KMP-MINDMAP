package ru.lipt.map.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.details.common.navigation.NodeDetailsNavigationDestinations
import ru.lipt.map.MR
import ru.lipt.map.ui.common.MindMapComponent
import ru.lipt.map.ui.models.MapViewScreenUi

@Composable
fun MapViewContent(
    screenModel: MapViewScreenModel,
) {
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model

    val navigator = LocalNavigator.currentOrThrow

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.UneditableDetailsScreen -> navigator.push(
                ScreenRegistry.get(
                    NodeDetailsNavigationDestinations.UneditableNodeDetailsScreenDestination(target.params)
                )
            )
            is NavigationTarget.NavigateUp -> navigator.pop()
        }
    }

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background, elevation = 0.dp,
                title = {
                    val title = ui.data?.title ?: stringResource(MR.strings.map_screen_app_bar_title_placeholder)
                    Text(
                        text = title, style = MindTheme.typography.material.h5
                    )
                },
                navigationIcon = {
                    IconButton(onClick = screenModel::onBackButtonClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = ""
                        )
                    }
                },
            )
        }) {

            ui.onSuccess {
                MindViewMapScreen(
                    ui = data,
                    onViewNodeClick = screenModel::onViewNodeClick,
                )
            }

            ui.onLoading {
                CircularProgressIndicatorLoadingScreen()
            }

            ui.onError { ErrorScreen(onRefresh = screenModel::init) }
        }
    }
}

@Composable
fun MindViewMapScreen(
    ui: MapViewScreenUi,
    onViewNodeClick: (String) -> Unit,
) {
    MindMapComponent(
        ui = ui.box,
        onViewNodeClick = onViewNodeClick,
    )
}
