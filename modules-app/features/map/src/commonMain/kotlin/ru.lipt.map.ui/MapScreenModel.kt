package ru.lipt.map.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.details.common.params.NodeDetailsScreenParams
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.map.common.params.MapScreenParams
import ru.lipt.map.ui.models.MapEdge
import ru.lipt.map.ui.models.MapNode
import ru.lipt.map.ui.models.MapScreenUi
import ru.lipt.map.ui.models.NodePosition

class MapScreenModel(
    private val params: MapScreenParams,
    private val mapInteractor: MindMapInteractor,
) : ScreenModel {
    private val _uiState: MutableScreenUiStateFlow<MapScreenUi, NavigationTarget> =
        MutableScreenUiStateFlow(MapScreenUi())
    val uiState = _uiState.asStateFlow()

    init {
        init()
    }

    fun init() {
        screenModelScope.launch {
            val map = mapInteractor.getMap(params.id)
            map ?: return@launch
            _uiState.updateUi {
                MapScreenUi(
                    nodes = map.nodes.associate {
                        it.id to MapNode(
                            id = it.id,
                            text = it.title,
                            childIds = it.childIds,
                        )
                    }
                )
            }
        }
    }

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    fun onAddClick(id: String) {
        _uiState.updateUi {
            val number = nodes.size.toString()
            copy(
                nodes = nodes.toMutableMap().apply {
                    this[id]?.let { node ->
                        this[id] = node.copy(childIds = node.childIds + listOf(number))
                    }
                } + mapOf(number to MapNode(number, "Node ${nodes.size}")),
                edges = edges + listOf(
                    MapEdge(
                        firstNodeId = id,
                        secondNodeId = number,
                        firstPosition = nodes[id]?.position ?: NodePosition()
                    )
                )
            )
        }
    }

    fun openNode(nodeId: String) {
        _uiState.navigateTo(
            NavigationTarget.DetailsScreen(
                NodeDetailsScreenParams(
                    mapId = params.id,
                    nodeId = nodeId,
                )
            )
        )
    }

    fun updatePosition(id: String, x: Float, y: Float) {
        _uiState.updateUi {
            copy(
                nodes = nodes.toMutableMap().apply {
                    this[id]?.let { node ->
                        this[id] = node.copy(position = NodePosition(x, y))
                    }
                },
                edges = edges.map { edge ->
                    if (edge.firstNodeId == id) edge.copy(
                        firstPosition = NodePosition(x, y)
                    )
                    else if (edge.secondNodeId == id) edge.copy(
                        secondPosition = NodePosition(x, y)
                    ) else edge
                }
            )
        }
    }

    fun onBackButtonClick() = _uiState.navigateTo(NavigationTarget.NavigateUp)

    companion object {
        const val ROOT_ID = "root_0"
    }
}
