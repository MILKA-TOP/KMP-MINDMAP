package ru.lipt.map.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderState
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import ru.lipt.core.compose.alert.EnterAlertDialogV2
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.coreui.shapes.MindShapes
import ru.lipt.coreui.shapes.RoundedCornerShape4
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.details.common.navigation.NodeDetailsNavigationDestinations
import ru.lipt.map.MR
import ru.lipt.map.PrivateMapNavigationDestinations
import ru.lipt.map.ui.models.EditMapNode
import ru.lipt.map.ui.models.MapNode
import ru.lipt.map.ui.models.MapScreenUi
import ru.lipt.map.ui.models.MindMapNodeVertex
import ru.lipt.map.ui.models.ViewMapNode

@Composable
fun MapContent(
    screen: Screen,
    screenModel: MapScreenModel,
) {
    screen.LifecycleEffect(
        onStarted = screenModel::onStarted
    )
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model

    val navigator = LocalNavigator.currentOrThrow

    screenModel.handleNavigation { target ->
        when (target) {
            is NavigationTarget.EditableDetailsScreen -> navigator.push(
                ScreenRegistry.get(
                    NodeDetailsNavigationDestinations.EditableNodeDetailsScreenDestination(target.params)
                )
            )
            is NavigationTarget.UneditableDetailsScreen -> navigator.push(
                ScreenRegistry.get(
                    NodeDetailsNavigationDestinations.UneditableNodeDetailsScreenDestination(target.params)
                )
            )
            is NavigationTarget.MapDetailsEditScreenDestination -> navigator.push(
                ScreenRegistry.get(
                    PrivateMapNavigationDestinations.MapEditDetails(target.params)
                )
            )
            is NavigationTarget.MapDetailsViewScreenDestination -> navigator.push(
                ScreenRegistry.get(
                    PrivateMapNavigationDestinations.MapViewDetails(target.params)
                )
            )
            is NavigationTarget.NavigateUp -> navigator.pop()
        }
    }

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    Scaffold(topBar = {
        TopAppBar(backgroundColor = MaterialTheme.colors.background, elevation = 0.dp, title = {
            val title = ui.data?.title ?: stringResource(MR.strings.map_screen_app_bar_title_placeholder)
            Text(
                text = title, style = MindTheme.typography.material.h5
            )
        }, navigationIcon = {
            IconButton(onClick = screenModel::onBackButtonClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = ""
                )
            }
        }, actions = {
            IconButton(onClick = screenModel::openMapDetails) {
                Icon(
                    imageVector = Icons.Filled.List, contentDescription = ""
                )
            }
        })
    }) {

        ui.onSuccess {
            MindMapScreen(
                ui = data,
                onCreateNewNode = screenModel::onCreateNewNode,
                onFieldTextChanged = screenModel::onFieldTextChanged,
                onConfirm = screenModel::onConfirm,
                onCancel = screenModel::onCancel,
                onNodeMoved = screenModel::onNodeMoved,
                onOpenDetailsNode = screenModel::onEditNodeClick,
                onViewNodeClick = screenModel::onViewNodeClick,
            )
        }

        ui.onLoading {
            CircularProgressIndicatorLoadingScreen()
        }

        ui.onError { ErrorScreen(onRefresh = screenModel::init) }
    }
}

@Composable
fun MindMapScreen(
    ui: MapScreenUi,
    onNodeMoved: (MapNode, Int) -> Unit,
    onCreateNewNode: (String) -> Unit,
    onOpenDetailsNode: (String) -> Unit,
    onFieldTextChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onViewNodeClick: (String) -> Unit,
) {
    val uiBox = ui.box
    val scale = remember { mutableStateOf(1f) }
    val panOffset = remember { mutableStateOf(Offset.Zero) }

    // Modifier for panning the canvas
    val panModifier = Modifier.pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            change.consume()
            panOffset.value += Offset(dragAmount.x / scale.value, dragAmount.y / scale.value)
        }
    }

    ui.alert?.let { alert ->
        PrivateAlertAddConfirm(
            ui = alert,
            onFieldTextChanged = onFieldTextChanged,
            onConfirm = onConfirm,
            onCancel = onCancel,
        )
    }

    Box {
//        ZoomSlider(scale = scale)
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().then(panModifier)
        ) {
            val nodeOffset = mutableStateOf(mutableMapOf<String, Offset>())
            val nodeSize = mutableStateOf(mutableMapOf<String, Size>())

            Canvas(modifier = Modifier.matchParentSize()) {
                uiBox.columns.forEach { topLevelNode ->
                    topLevelNode.groups.map { group ->
                        val start = group.parentNodeId
                        start?.let { startNodeId ->
                            nodeOffset.value[startNodeId]?.let { startPos ->
                                nodeSize.value[startNodeId]?.let { startSize ->
                                    // Calculate the start point as the right edge center of the node
                                    val startOffset = startPos + Offset(startSize.width, -startSize.height / 2)

                                    group.nodes.map { end ->
                                        nodeOffset.value[end.nodeId]?.let { endPos ->
                                            // For the end point, we keep it at the vertical center, but do not need to adjust for width since it's the starting point of the line
                                            val endOffset = endPos + Offset(0f, -(nodeSize.value[end.nodeId]?.height ?: 0f) / 2)

                                            drawLine(
                                                color = Color.Black, start = startOffset, end = endOffset, strokeWidth = 2.dp.toPx()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val reorderStates = mutableStateOf(mutableMapOf<String, ReorderState<MapNode>>())
            val dragCatalogInProgress = mutableStateOf<String?>(null)

            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                ) {
                    uiBox.columns.forEach { colum ->
                        Column(modifier = Modifier.padding(48.dp)) {
                            colum.groups.map {
                                val reorderState = reorderStates.value.getOrPut(it.parentNodeId.orEmpty()) { rememberReorderState() }
                                NodeColumn(
                                    group = it,
                                    nodeOffset = nodeOffset,
                                    onNodeMoved = onNodeMoved,
                                    panOffset = panOffset.value,
                                    nodeSize = nodeSize,
                                    reorderState = reorderState,
                                    onCreateNewNode = onCreateNewNode,
                                    onOpenDetailsNode = onOpenDetailsNode,
                                    dragCatalogInProgress = dragCatalogInProgress,
                                    onViewNodeClick = onViewNodeClick,
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                            Spacer(modifier = Modifier.height(48.dp))
                        }
                    }
                    Spacer(modifier = Modifier.size(128.dp))
                }
            }
        }
    }
}

@Composable
fun NodeColumn(
    onNodeMoved: (MapNode, Int) -> Unit,
    onCreateNewNode: (String) -> Unit,
    onOpenDetailsNode: (String) -> Unit,
    onViewNodeClick: (String) -> Unit,
    group: MindMapNodeVertex,
    nodeOffset: MutableState<MutableMap<String, Offset>>, panOffset: Offset,
    nodeSize: MutableState<MutableMap<String, Size>>,
    reorderState: ReorderState<MapNode>,
    dragCatalogInProgress: MutableState<String?>,
) {
    val positionState = remember { mutableStateOf(Offset.Zero) }
    val lazyListState = rememberLazyListState()
    val panModifier = Modifier.offset { IntOffset(panOffset.x.toInt(), panOffset.y.toInt()) }.onGloballyPositioned { layoutCoordinates ->
        val position = layoutCoordinates.positionInRoot()
        positionState.value = Offset(position.x, position.y)
    }

    fun offsetSizeModifier(nodeId: String) = Modifier.onGloballyPositioned { layoutCoordinates ->
        val position = layoutCoordinates.positionInRoot()
        val size = layoutCoordinates.size
        nodeOffset.value[nodeId] = Offset(position.x, position.y)
        nodeSize.value[nodeId] = Size(width = size.width.toFloat(), height = size.height.toFloat())
    }

    if (group.parentNodeId == null) {
        when (val rootNode = group.nodes.first()) {
            is ViewMapNode -> ViewNodeItem(
                ui = rootNode,
                isRoot = true,
                onClick = onViewNodeClick,
                modifier = panModifier.then(offsetSizeModifier(rootNode.nodeId)),

                )
            is EditMapNode -> NodeItem(
                modifier = panModifier.then(offsetSizeModifier(rootNode.nodeId)),
                ui = rootNode,
                isRoot = true,
                isEnabled = dragCatalogInProgress.value == null,
                onCreateNewNode = onCreateNewNode,
                onOpenDetailsNode = onOpenDetailsNode,
            )
        }
    } else {
        val items = remember(group.nodes) { mutableStateOf(group.nodes) }
        ReorderContainer(
            modifier = panModifier,
            state = reorderState,
        ) {
            LazyColumn(
                modifier = Modifier
            ) {
                items(items.value) {
                    when (it) {
                        is ViewMapNode -> ViewNodeItem(
                            ui = it,
                            onClick = onViewNodeClick,
                            modifier = panModifier.then(offsetSizeModifier(it.nodeId)),

                            )
                        is EditMapNode -> NodeItemReorderable(reorderState = reorderState,
                            node = it,
                            nodeOffset = nodeOffset,
                            items = items,
                            lazyListState = lazyListState,
                            isEnabled = dragCatalogInProgress.value == null || it.parentNodeId == dragCatalogInProgress.value,
                            onCreateNewNode = onCreateNewNode,
                            onOpenDetailsNode = onOpenDetailsNode,
                            onNodeMoved = onNodeMoved,
                            modifier = offsetSizeModifier(it.nodeId),
                            onStartDragging = {
                                dragCatalogInProgress.value = it
                            },
                            onEndDragging = {
                                dragCatalogInProgress.value = null
                            })
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun LazyItemScope.NodeItemReorderable(
    reorderState: ReorderState<MapNode>,
    node: EditMapNode,
    lazyListState: LazyListState,
    nodeOffset: MutableState<MutableMap<String, Offset>>,
    items: MutableState<List<MapNode>>,
    isEnabled: Boolean = true,
    onNodeMoved: (MapNode, Int) -> Unit,
    onCreateNewNode: (String) -> Unit,
    onOpenDetailsNode: (String) -> Unit,
    onStartDragging: (String?) -> Unit,
    onEndDragging: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    ReorderableItem(modifier = Modifier.then(modifier), state = reorderState, key = node.nodeId, // Unique key for each reorderable item
        data = node, // Data to be passed to the drop target
        onDragEnter = { state ->
            items.value = items.value.toMutableList().apply {
                val index = indexOf(node)
                if (index == -1) return@ReorderableItem
                remove(state.data)
                add(index, state.data)
                onStartDragging(node.parentNodeId)

                scope.launch {
                    handleLazyListScroll(
                        lazyListState = lazyListState,
                        dropIndex = index,
                    )
                }
            }
        }, onDrop = { state -> // Data passed from the draggable item
            val index = items.value.indexOf(node)
            onEndDragging()
            onNodeMoved(node, index)
        }, draggableContent = {
            NodeItem(ui = node,
                isEnabled = isEnabled,
                onCreateNewNode = onCreateNewNode,
                onOpenDetailsNode = onOpenDetailsNode,
                modifier = Modifier.shadow(elevation = 20.dp).onGloballyPositioned { layoutCoordinates ->
                    val position = layoutCoordinates.positionInRoot()
                    nodeOffset.value[node.nodeId] = Offset(position.x, position.y)
                })
        }) {
        NodeItem(
            ui = node,
            isEnabled = isEnabled,
            onCreateNewNode = onCreateNewNode,
            onOpenDetailsNode = onOpenDetailsNode,
        )
    }
}

@Composable
private fun NodeItem(
    ui: EditMapNode,
    isEnabled: Boolean,
    onCreateNewNode: (String) -> Unit,
    onOpenDetailsNode: (String) -> Unit,
    isRoot: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var popUpState by remember { mutableStateOf(false) }
    if (popUpState) {
        Popup(alignment = Alignment.CenterEnd,
//            offset = popUpOffset,
            onDismissRequest = { popUpState = false }) {
            Column {
                Button(
                    shape = RoundedCornerShape4,
                    onClick = {
                        onCreateNewNode(ui.nodeId)
                        popUpState = false
                    }) {
                    Text(text = stringResource(MR.strings.map_screen_add_node))
                }
                Button(
                    shape = RoundedCornerShape4,
                    onClick = {
                        onOpenDetailsNode(ui.nodeId)
                        popUpState = false
                    }) {
                    Text(text = stringResource(MR.strings.map_screen_details))
                }
            }
        }
    }
    Card(modifier = (Modifier.alpha(0.5f).takeIf { !isEnabled } ?: Modifier).then(modifier).clip(MindShapes.medium)
        .clickable(onClick = { popUpState = true })
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = ui.title,
            style = if (isRoot) MaterialTheme.typography.h6 else MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun ViewNodeItem(
    ui: ViewMapNode,
    isRoot: Boolean = false,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = Modifier.then(modifier).clip(MindShapes.medium)
            .clickable(onClick = { onClick(ui.nodeId) }),
        backgroundColor = if (ui.isMarked) MaterialTheme.colors.surface else MindTheme.colors.unmarkedNode
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = ui.title,
            style = if (isRoot) MaterialTheme.typography.h6 else MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun PrivateAlertAddConfirm(
    ui: MapScreenUi.EnterNewNodeTitle,
    onFieldTextChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    EnterAlertDialogV2(
        text = stringResource(MR.strings.map_screen_enter_title_message),
        confirmText = stringResource(MR.strings.map_screen_enter_title_confirm_button),
        cancelText = stringResource(MR.strings.map_screen_enter_title_cancel_button),
        fieldLabel = stringResource(MR.strings.map_screen_enter_title_field_label),
        maxSymbols = 25,
        onConfirm = onConfirm,
        onCancel = onCancel,
        inProgress = ui.inProgress,
        confirmButtonEnabled = ui.isConfirmButtonEnabled,
        fieldTextValue = ui.title,
        onFieldTextChanged = onFieldTextChanged,
    )
}
