package ru.lipt.catalog.search

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.catalog.main.models.CatalogScreenUi.Companion.toUi
import ru.lipt.catalog.search.models.SearchAlerts
import ru.lipt.catalog.search.models.SearchContentUi
import ru.lipt.catalog.search.models.SearchScreenUi
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.error
import ru.lipt.core.idle
import ru.lipt.core.loading
import ru.lipt.core.success
import ru.lipt.domain.catalog.ICatalogInteractor
import ru.lipt.domain.catalog.models.CatalogMindMap
import ru.lipt.map.common.params.MapScreenParams

class SearchScreenModel(
    private val catalogInteractor: ICatalogInteractor,
) : ScreenModel {
    private val _uiState: MutableScreenUiStateFlow<SearchScreenUi, NavigationTarget> = MutableScreenUiStateFlow(SearchScreenUi())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var privateMapAdd: Job? = null
    private var publicMapAdd: Job? = null
    private var selectedMapId: String? = null
    private var privateMapPassword: String = ""
    private var maps: List<CatalogMindMap> = emptyList()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onSearchTextChanged(text: String) {
        val inputText = text.trimStart()
        if (inputText == text) {
            _uiState.updateUi {
                copy(searchText = inputText, content = (idle<SearchContentUi, Unit>()).takeIf { !inputText.isQueryValidated() } ?: content)
            }

            loadMaps()
        }
    }

    fun loadMaps() {
        searchJob?.cancel()
        val query = _uiState.ui.searchText
        if (!query.isQueryValidated()) return

        searchJob = screenModelScope.launchCatching(catchBlock = {
            _uiState.updateUi { copy(content = Unit.error()) }
        }) {
            _uiState.updateUi { copy(content = loading()) }

            delay(SEARCH_DELAY)
            maps = catalogInteractor.search(query)
            val uiElements = maps
            _uiState.updateUi { copy(content = SearchContentUi(uiElements.toUi()).success()) }
        }
    }

    fun onHideAddAlert() {
        privateMapAdd?.cancel()
        publicMapAdd?.cancel()
        selectedMapId = null
        privateMapPassword = ""
        _uiState.updateUi { copy(addMindMapAlert = null) }
    }

    fun onMapElementClick(mapId: String) {
        val clickedMap = maps.firstOrNull { it.id == mapId } ?: return
        selectedMapId = clickedMap.id
        val clickedMapTitle = clickedMap.title
        when {
            clickedMap.isSaved -> _uiState.navigateTo(NavigationTarget.ToMapNavigate(MapScreenParams(mapId)))
            clickedMap.isPrivate -> _uiState.updateUi { copy(addMindMapAlert = SearchAlerts.PrivateMap(clickedMapTitle)) }
            else -> _uiState.updateUi { copy(addMindMapAlert = SearchAlerts.PublicMap(clickedMapTitle)) }
        }
    }

    fun onConfirmAddPublicMapAlert() {
        privateMapAdd?.cancel()
        publicMapAdd?.cancel()
        val mapId = selectedMapId ?: return
        publicMapAdd = screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi { copy(addMindMapAlert = addMindMapAlert?.copy(false)) }
            }
        ) {
            _uiState.updateUi { copy(addMindMapAlert = addMindMapAlert?.copy(true)) }

            catalogInteractor.addMap(mapId)
            _uiState.navigateTo(NavigationTarget.ToMapNavigate(MapScreenParams(mapId)))
        }
    }

    fun onConfirmAddPrivateMapAlert() {
        privateMapAdd?.cancel()
        publicMapAdd?.cancel()
        val mapId = selectedMapId ?: return
        val password = privateMapPassword.takeIf { it.isNotEmpty() } ?: return
        publicMapAdd = screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi { copy(addMindMapAlert = addMindMapAlert?.copy(false)) }
            }
        ) {
            _uiState.updateUi { copy(addMindMapAlert = addMindMapAlert?.copy(true)) }

            catalogInteractor.addMap(mapId, password)
            _uiState.navigateTo(NavigationTarget.ToMapNavigate(MapScreenParams(mapId)))
        }
    }

    fun onPasswordEnter(password: String) {
        privateMapPassword = password
        _uiState.updateUi {
            copy(
                addMindMapAlert = (addMindMapAlert as? SearchAlerts.PrivateMap)
                    ?.copy(password = password.trimStart())
            )
        }
    }

    private fun String.isQueryValidated(): Boolean = this.length >= MINIMAL_QUERY_LENGTH

    companion object {
        private const val MINIMAL_QUERY_LENGTH = 3
        private const val SEARCH_DELAY = 250L
    }
}
