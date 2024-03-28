package ru.lipt.map.ui.view

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.LoadingState
import ru.lipt.core.cache.CachePolicy
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.error
import ru.lipt.core.idle
import ru.lipt.core.loading
import ru.lipt.core.success
import ru.lipt.details.common.params.NodeDetailsScreenParams
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote
import ru.lipt.map.common.params.MapViewScreenParams
import ru.lipt.map.ui.common.toViewBoxUi
import ru.lipt.map.ui.models.MapViewScreenUi

class MapViewScreenModel(
    private val params: MapViewScreenParams,
    private val mapInteractor: MindMapInteractor,
) : ScreenModel {
    private val _uiState: MutableScreenUiStateFlow<LoadingState<MapViewScreenUi, Unit>, NavigationTarget> = MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

    private var _map: SummaryMapResponseRemote? = null

    init {
        init()
    }

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

    fun onBackButtonClick() = _uiState.navigateTo(NavigationTarget.NavigateUp)

    private fun SummaryViewMapResponseRemote.toViewUi() = MapViewScreenUi(
        title = title, box = toViewBoxUi()
    )

    fun onViewNodeClick(nodeId: String) {
        _uiState.navigateTo(NavigationTarget.UneditableDetailsScreen(NodeDetailsScreenParams(params.id, nodeId, params.userId)))
    }

    private suspend fun getMap(): MapViewScreenUi {
        val map = mapInteractor.fetchViewMap(params.id, params.userId, cachePolicy = CachePolicy.REFRESH)
        _map = map

        return map.toViewUi()
    }
}
