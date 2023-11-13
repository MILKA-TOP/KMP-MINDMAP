package ru.lipt.map.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.map.ui.models.MapEdge
import ru.lipt.map.ui.models.MapNode
import ru.lipt.map.ui.models.MapScreenUi
import ru.lipt.map.ui.models.NodePosition
import ru.lipt.navigation.params.map.MapScreenParams

class MapScreenModel(
    private val params: MapScreenParams,
    private val mapInteractor: MindMapInteractor,
) : ScreenModel {
    private val _uiState: MutableStateFlow<MapScreenUi> = MutableStateFlow(MapScreenUi())
    val uiState = _uiState.asStateFlow()

    init {
        init()
    }

    fun init() {
        screenModelScope.launch {
            val map = mapInteractor.getMap(params.id)
            map ?: return@launch
            _uiState.value = MapScreenUi(
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

    fun onAddClick(id: String) {
        _uiState.update {
            val number = it.nodes.size.toString()
            it.copy(
                nodes = it.nodes.toMutableMap().apply {
                    this[id]?.let { node ->
                        this[id] = node.copy(childIds = node.childIds + listOf(number))
                    }
                } + mapOf(number to MapNode(number, "Node ${it.nodes.size}")),
                edges = it.edges + listOf(
                    MapEdge(
                        firstNodeId = id,
                        secondNodeId = number,
                        firstPosition = it.nodes[id]?.position ?: NodePosition()
                    )
                )
            )
        }
    }

    fun updatePosition(id: String, x: Float, y: Float) {
        _uiState.update {
            it.copy(
                nodes = it.nodes.toMutableMap().apply {
                    this[id]?.let { node ->
                        this[id] = node.copy(position = NodePosition(x, y))
                    }
                },
                edges = it.edges.map { edge ->
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

    companion object {
        const val ROOT_ID = "root_0"
    }
}
