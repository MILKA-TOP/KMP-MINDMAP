package ru.lipt.catalog.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.catalog.MR
import ru.lipt.catalog.main.models.CatalogScreenUi
import ru.lipt.catalog.models.MapCatalogElement
import ru.lipt.catalog.navigation.PrivateCatalogDestinations
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.coreui.shapes.RoundedCornerShape12
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.login.common.navigation.LoginNavigationDestinations
import ru.lipt.map.common.navigation.MapNavigationDestinations

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CatalogContent(
    screen: Screen,
    screenModel: CatalogScreenModel = screen.getScreenModel(),
) {
    screen.LifecycleEffect(
        onStarted = screenModel::onStarted
    )

    val scrollState = rememberLazyListState()
    val navigator = LocalNavigator.currentOrThrow

    val uiState = screenModel.uiState.collectAsState().value

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.MapDestination -> navigator.push(
                ScreenRegistry.get(
                    MapNavigationDestinations.MapScreenDestination(target.params)
                )
            )
            NavigationTarget.EnterPinScreenDestination -> {
                navigator.replaceAll(
                    ScreenRegistry.get(
                        LoginNavigationDestinations.PinInputScreenDestination
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
            NavigationTarget.SearchMapDestination -> {
                navigator.push(
                    ScreenRegistry.get(
                        PrivateCatalogDestinations.SearchDestination
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
                backgroundColor = MaterialTheme.colors.background,
                title = {
                    Text(
                        text = stringResource(MR.strings.catalog_screen_title),
                        style = MindTheme.typography.material.h5
                    )
                },
                navigationIcon = {
                    IconButton(onClick = screenModel::logout) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    IconButton(onClick = screenModel::onPullToRefresh) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = ""
                        )
                    }
                    IconButton(onClick = screenModel::createNewMindMap) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircle,
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
            onMapElementClick = screenModel::onMapElementClick,
            onSearchElementClick = screenModel::searchMindMap,
            onPullToRefresh = screenModel::onPullToRefresh,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Content(
    ui: CatalogScreenUi,
    scrollState: LazyListState,
    onMapElementClick: (String) -> Unit,
    onSearchElementClick: () -> Unit,
    onPullToRefresh: () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(ui.isLoadingInProgress, onRefresh = onPullToRefresh)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .sizeIn(maxWidth = 360.dp)
                .fillMaxHeight()
                .align(Alignment.TopCenter)
                .padding(all = 16.dp)
                .pullRefresh(pullRefreshState),
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            if (it.isFocused) onSearchElementClick()
                        },
                    value = "",
                    onValueChange = {},
                    label = { Text(stringResource(MR.strings.catalog_screen_field_search_label)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = ""
                        )
                    }
                )
                Spacer(Modifier.height(12.dp))
            }
            item {
                Text(
                    text = stringResource(MR.strings.catalog_map_saved_maps_title),
                    style = MindTheme.typography.material.h5
                )
                Spacer(Modifier.height(16.dp))
            }
            items(ui.maps) { map ->
                MapListElement(
                    map = map,
                    onMapElementClick = onMapElementClick,
                )
                Spacer(Modifier.height(16.dp))
            }
        }
        PullRefreshIndicator(
            refreshing = ui.isLoadingInProgress,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
fun MapListElement(
    map: MapCatalogElement,
    onMapElementClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape12)
            .background(MaterialTheme.colors.surface)
            .clickable(onClick = { onMapElementClick(map.id) })
            .padding(all = 16.dp)
    ) {
        if (map.showFirstTypeActionLine) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(map.type.stringRes),
                    style = MaterialTheme.typography.caption,
                )
                if (map.isSaved) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        modifier = Modifier.size(12.dp),
                        imageVector = Icons.Filled.Star,
                        contentDescription = ""
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text = map.title,
            style = MaterialTheme.typography.h6,
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (map.description.isNotEmpty()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = map.description,
                style = MaterialTheme.typography.body1,
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(MR.strings.catalog_map_saved_author_format_caption, map.adminEmail),
                style = MaterialTheme.typography.caption,
            )
            if (map.showPrivateIcon) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    modifier = Modifier.size(12.dp),
                    imageVector = Icons.Filled.Lock,
                    contentDescription = ""
                )
            }
        }
    }
}
