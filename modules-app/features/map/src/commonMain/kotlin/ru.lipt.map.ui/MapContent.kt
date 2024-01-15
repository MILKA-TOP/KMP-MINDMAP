package ru.lipt.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.core.compose.alert.EnterAlertDialog
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.details.common.navigation.NodeDetailsNavigationDestinations
import ru.lipt.map.ui.models.MapEdge
import ru.lipt.map.ui.models.MapNode
import ru.lipt.map.ui.models.MapScreenUi
import kotlin.math.roundToInt

@Composable
fun MapContent(
    screenModel: MapScreenModel,
) {
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

            is NavigationTarget.NavigateUp -> navigator.pop()
        }
    }

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Map Editor") },
                navigationIcon = {
                    IconButton(onClick = screenModel::onBackButtonClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) {

        ui.onSuccess {
            MapContent(
                ui = data,
                addNode = screenModel::onAddClick,
                openNode = screenModel::openNode,
                updatePosition = screenModel::updatePosition,
                onEnterNewNodeTitle = screenModel::onConfirmNewNodeAlert,
                closeNewNodeAlert = screenModel::closeNewNodeAlert,
            )
        }

        ui.onLoading {
            CircularProgressIndicatorLoadingScreen()
        }

        ui.onError { ErrorScreen(onRefresh = screenModel::init) }
    }
}

@Composable
private fun MapContent(
    ui: MapScreenUi,
    addNode: (String) -> Unit,
    openNode: (String) -> Unit,
    updatePosition: (String, Float, Float) -> Unit,
    onEnterNewNodeTitle: (String) -> Unit,
    closeNewNodeAlert: () -> Unit,
) {

    ui.newNodeAlert?.let { alert ->
        EnterAlertDialog(
            title = "New Node",
            text = "Please, enter the title of new node",
            fieldLabel = "Title",
            confirmText = "Enter",
            cancelText = "Cancel",
            onConfirm = onEnterNewNodeTitle,
            inProgress = alert.inProgress,
            onDismissRequest = closeNewNodeAlert,
            onCancel = closeNewNodeAlert,
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        ui.edges.forEach { edge ->
            NodeEdge(edge)
        }

        ui.nodes.forEach { node ->
            MindNode(
                node = node.value,
                addNode = addNode,
                openNode = openNode,
                onUpdatePosition = updatePosition,
            )
        }
    }
}

@Composable
private fun MindNode(
    node: MapNode,
    addNode: (String) -> Unit,
    openNode: (String) -> Unit,
    onUpdatePosition: (String, Float, Float) -> Unit = { _, _, _ -> },
) {
    val borderColor = Color.Magenta

    var popUpState by remember { mutableStateOf(false) }
    var popUpOffset by remember { mutableStateOf(IntOffset(0, 0)) }
    DraggableItem(
        onUpdatePosition = { x, y -> onUpdatePosition(node.id, x, y) },
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(4.dp, borderColor)
                .background(borderColor.copy(alpha = 0.4f))
                .onGloballyPositioned {
                    popUpOffset = IntOffset(it.size.width, it.size.height / 2)
                }
                .clickable {
                    popUpState = true
                }
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center).padding(all = 8.dp),
                text = node.text,
            )
            if (popUpState) {
                Popup(alignment = Alignment.Center,
                    offset = popUpOffset,
                    onDismissRequest = { popUpState = false }
                ) {
                    Column {
                        Button(onClick = {
                            addNode(node.id)
                            popUpState = false
                        }) {
                            Text(text = "+")
                        }
                        Button(onClick = {
                            openNode(node.id)
                            popUpState = false
                        }) {
                            Text(text = "Details")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NodeEdge(edge: MapEdge) {
    Spacer(
        modifier = Modifier
            .drawWithCache {
                val path = Path()
                path.moveTo(
                    edge.firstPosition.x,
                    edge.firstPosition.y,
                )
                path.quadraticBezierTo(
                    edge.middlePosition.x,
                    edge.middlePosition.y,
                    edge.secondPosition.x,
                    edge.secondPosition.y,
                )
                path.close()
                onDrawBehind {
                    drawPath(path, Color.Magenta, style = Stroke(width = 10f))
                }
            }
            .fillMaxSize()
    )
}

@Composable
private fun DraggableItem(
    onUpdatePosition: (Float, Float) -> Unit = { _, _ -> },
    onItemClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    Box(modifier = Modifier
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .pointerInput(Unit) {
            dragGestures(
                onDragStart = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            ) { change, dragAmount ->

                change.consume()
                offsetX += dragAmount.x
                offsetY += dragAmount.y
                onUpdatePosition(offsetX, offsetY)
            }
            detectTapGestures {
                onItemClick()
            }
        }) {
        content()
    }
}
