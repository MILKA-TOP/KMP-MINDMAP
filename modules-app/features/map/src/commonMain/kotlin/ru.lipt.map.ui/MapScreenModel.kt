package ru.lipt.map.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.LoadingState
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.error
import ru.lipt.core.idle
import ru.lipt.core.loading
import ru.lipt.core.success
import ru.lipt.details.common.params.NodeDetailsScreenParams
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.domain.map.models.MapType
import ru.lipt.domain.map.models.MindMap
import ru.lipt.map.common.params.MapScreenParams
import ru.lipt.map.ui.models.MapEdge
import ru.lipt.map.ui.models.MapNode.Companion.toUi
import ru.lipt.map.ui.models.MapScreenUi
import ru.lipt.map.ui.models.NewNodeAlertModel
import ru.lipt.map.ui.models.NodePosition

class MapScreenModel(
    private val params: MapScreenParams,
    private val mapInteractor: MindMapInteractor,
) : ScreenModel {
    private val _uiState: MutableScreenUiStateFlow<LoadingState<MapScreenUi, Unit>, NavigationTarget> =
        MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

    private var _map: MindMap? = null
    private var _mapType: MapType = MapType.VIEW
    private var parentIdNewNode: String? = null

    private var addNewNodeJob: Job? = null

    init {
        init()
    }

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun init() {
        screenModelScope.launchCatching(
            catchBlock = {
                _uiState.updateUi { Unit.error() }
            }
        ) {
            _uiState.updateUi { loading() }
            delay(2_000L)
            val map = mapInteractor.getMap(params.id)
            _map = map
            _mapType = map.viewType
            _uiState.updateUi {
                MapScreenUi(
                    nodes = map.nodes.associate {
                        it.id to it.toUi()
                    }
                ).success()
            }
        }
    }

    fun onAddClick(id: String) {
        parentIdNewNode = id
        _uiState.updateUi { copy { it.copy(newNodeAlert = NewNodeAlertModel()) } }
    }

    fun closeNewNodeAlert() {
        addNewNodeJob?.cancel()
        hideNewNodeAlert()
    }

    fun onConfirmNewNodeAlert(title: String) {
        val parentIdNode = parentIdNewNode ?: return
        val map = _map ?: return
        val trimmedTitle = title.trim()

        addNewNodeJob?.cancel()

        addNewNodeJob = screenModelScope.launchCatching(
            catchBlock = {
                _uiState.updateUi { copy { it.copy(newNodeAlert = it.newNodeAlert?.copy(inProgress = false)) } }
            },
            finalBlock = {
                hideNewNodeAlert()
            }
        ) {
            _uiState.updateUi { copy { it.copy(newNodeAlert = it.newNodeAlert?.copy(inProgress = true)) } }

            delay(1_000L)
            val newNode = mapInteractor.createNewNode(map.id, parentIdNode, trimmedTitle)

            _uiState.updateUi {
                copy { map ->
                    map.copy(
                        nodes = map.nodes + mapOf(newNode.id to newNode.toUi()),
                        edges = map.edges + MapEdge(
                            firstNodeId = parentIdNode,
                            secondNodeId = newNode.id,
                            firstPosition = map.nodes[parentIdNode]?.position ?: NodePosition()
                        )
                    )
                }
            }
        }
    }

    fun openNode(nodeId: String) {
        val viewType = _map?.viewType ?: return

        _uiState.navigateTo(
            when (viewType) {
                MapType.EDIT -> NavigationTarget.EditableDetailsScreen(
                    NodeDetailsScreenParams(
                        mapId = params.id,
                        nodeId = nodeId,
                    )
                )
                else -> NavigationTarget.UneditableDetailsScreen(
                    NodeDetailsScreenParams(
                        mapId = params.id,
                        nodeId = nodeId,
                    )
                )
            }
        )
    }

    fun updatePosition(id: String, x: Float, y: Float) {
        _uiState.updateUi {
            copy { map ->
                map.copy(

                    nodes = map.nodes.toMutableMap().apply {
                        this[id]?.let { node ->
                            this[id] = node.copy(position = NodePosition(x, y))
                        }
                    },
                    edges = map.edges.map { edge ->
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
    }

    fun onBackButtonClick() = _uiState.navigateTo(NavigationTarget.NavigateUp)

    private fun hideNewNodeAlert() {
        parentIdNewNode = null
        _uiState.updateUi { copy { it.copy(newNodeAlert = null) } }
    }

    companion object {
        const val ROOT_ID = "root_0"
    }
}
