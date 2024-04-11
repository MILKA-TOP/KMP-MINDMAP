package ru.lipt.map.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.LoadingState
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.error
import ru.lipt.core.idle
import ru.lipt.core.loading
import ru.lipt.core.success
import ru.lipt.details.common.params.NodeDetailsScreenParams
import ru.lipt.domain.map.IMindMapInteractor
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote
import ru.lipt.map.common.params.MapScreenParams
import ru.lipt.map.ui.common.toViewBoxUi
import ru.lipt.map.ui.models.EditMapNode
import ru.lipt.map.ui.models.MapScreenUi
import ru.lipt.map.ui.models.MindMapBox
import ru.lipt.map.ui.models.MindMapColumn
import ru.lipt.map.ui.models.MindMapNodeVertex

class MapScreenModel(
    private val params: MapScreenParams,
    private val mapInteractor: IMindMapInteractor,
) : ScreenModel {
    private val _uiState: MutableScreenUiStateFlow<LoadingState<MapScreenUi, Unit>, NavigationTarget> = MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

    private var _map: SummaryMapResponseRemote? = null
    private var _mapType: MapType = MapType.VIEW
    private var _parentNodeIdAction: String? = null
    private var newNodeTitle: String = ""

    init {
        init()
    }

    fun onStarted() = init()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun init() {
        screenModelScope.launchCatching(catchBlock = {
            _uiState.showAlertError(UiError.Alert.Default(message = it.stackTraceToString()))
            _uiState.updateUi { Unit.error() }
        }) {
            _uiState.updateUi { loading() }
            val mapUi = getMap()
            _uiState.updateUi { mapUi.success() }
        }
    }

    fun onCreateNewNode(id: String) {
        _parentNodeIdAction = id
        _uiState.updateUi {
            copy { ui ->
                ui.copy(
                    alert = MapScreenUi.EnterNewNodeTitle()
                )
            }
        }
    }

    fun onBackButtonClick() = _uiState.navigateTo(NavigationTarget.NavigateUp)
    fun openMapDetails() {
        when (_mapType) {
            MapType.EDIT -> _uiState.navigateTo(NavigationTarget.MapDetailsEditScreenDestination(params))
            MapType.VIEW, MapType.INTRACTABLE -> _uiState.navigateTo(NavigationTarget.MapDetailsViewScreenDestination(params))
        }
    }

    private fun SummaryViewMapResponseRemote.toViewUi() = MapScreenUi(
        title = title, isSaveButtonVisible = false, box = toViewBoxUi()
    )

    private fun SummaryEditMapResponseRemote.toEditUi() = MapScreenUi(
        title = title, isSaveButtonVisible = true, box = toEditBoxUi()
    )

    private fun SummaryEditMapResponseRemote.toEditBoxUi(): MindMapBox {
        val rootNode = this.nodes.first { it.parentNodeId == null }
        val rootColumn = MindMapColumn(
            listOf(
                MindMapNodeVertex(
                    nodes = listOf(
                        EditMapNode(
                            nodeId = rootNode.id, title = title, priorityPosition = 0
                        )
                    )
                )
            )
        )
        val columns = mutableListOf(rootColumn)

        var i = 0
        while (true) {
            val previousColumn = columns[i]
            val currentGroups = mutableListOf<MindMapNodeVertex>()
            previousColumn.groups.map { group ->
                group.nodes.map { parentNode ->
                    val children = nodes.filter { it.parentNodeId == parentNode.nodeId }.sortedBy { it.priorityPosition }
                    if (children.isNotEmpty()) {
                        currentGroups.add(MindMapNodeVertex(parentNodeId = parentNode.nodeId,
                            nodes = children.sortedBy { it.priorityPosition }.map {
                                EditMapNode(
                                    nodeId = it.id,
                                    title = it.label,
                                    parentNodeId = parentNode.nodeId,
                                    priorityPosition = it.priorityPosition,
                                )
                            })
                        )
                    }
                }
            }
            if (currentGroups.isEmpty()) break

            i++
            columns.add(MindMapColumn(groups = currentGroups))
        }
        return MindMapBox(columns = columns)
    }

    fun onFieldTextChanged(s: String) {
        newNodeTitle = s
        _uiState.updateUi {
            copy { ui ->
                ui.copy(alert = ui.alert?.copy(title = s))
            }
        }
    }

    fun onConfirm() {
        val parentNodeId = _parentNodeIdAction ?: return
        val newNodeTitle = newNodeTitle.trim()
        screenModelScope.launchCatching {
            val updatedMap = mapInteractor.addNewNodeToMap(params.id, parentNodeId, newNodeTitle)

            _uiState.updateUi {
                copy { ui ->
                    updatedMap.toUI()
                }
            }
        }
        onCancel()
    }

    fun onCancel() {
        newNodeTitle = ""
        _parentNodeIdAction = null
        _uiState.updateUi {
            copy { ui ->
                ui.copy(alert = null)
            }
        }
    }

    fun saveMindMap() {
        screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi { copy { it.copy(updateInProgress = false) } }
            }
        ) {
            _uiState.updateUi { copy { it.copy(updateInProgress = true) } }
            delay(250L)
            val mapUi = updateMap()

            _uiState.updateUi { copy { mapUi } }
        }
    }

    fun onEditNodeClick(nodeId: String) {
        _uiState.navigateTo(NavigationTarget.EditableDetailsScreen(NodeDetailsScreenParams(params.id, nodeId)))
    }

    fun onViewNodeClick(nodeId: String) {
        _uiState.navigateTo(NavigationTarget.UneditableDetailsScreen(NodeDetailsScreenParams(params.id, nodeId)))
    }

    private suspend fun updateMap(): MapScreenUi {
        val map = mapInteractor.updateMindMap(params.id)
        updateUiMap(map)

        return map.toUI()
    }

    private suspend fun getMap(): MapScreenUi {
        val map = mapInteractor.getMap(params.id)
        updateUiMap(map)

        return map.toUI()
    }

    private fun updateUiMap(map: SummaryMapResponseRemote) {
        _map = map
        _mapType = when (map) {
            is SummaryViewMapResponseRemote -> MapType.VIEW
            is SummaryEditMapResponseRemote -> MapType.EDIT
            else -> throw IllegalArgumentException()
        }
    }

    private fun SummaryMapResponseRemote.toUI() = when (this) {
        is SummaryViewMapResponseRemote -> toViewUi()
        is SummaryEditMapResponseRemote -> toEditUi()
        else -> throw IllegalArgumentException()
    }

    private enum class MapType {
        VIEW, INTRACTABLE, EDIT
    }

    companion object {
        const val ROOT_ID = "root_0"
    }
}
