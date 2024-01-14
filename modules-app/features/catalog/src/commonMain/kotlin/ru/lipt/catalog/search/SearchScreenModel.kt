package ru.lipt.catalog.search

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.catalog.search.models.EnterAlertModel
import ru.lipt.catalog.search.models.MapQueryElement
import ru.lipt.catalog.search.models.SearchContentUi
import ru.lipt.catalog.search.models.SearchScreenUi
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.error
import ru.lipt.core.idle
import ru.lipt.core.loading
import ru.lipt.core.success
import ru.lipt.domain.catalog.CatalogInteractor
import ru.lipt.domain.catalog.models.MindMapQueryResponse
import ru.lipt.map.common.params.MapScreenParams

class SearchScreenModel(
    private val catalogInteractor: CatalogInteractor,
) : ScreenModel {
    private val _uiState: MutableScreenUiStateFlow<SearchScreenUi, NavigationTarget> =
        MutableScreenUiStateFlow(SearchScreenUi())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var privateMapAdd: Job? = null
    private var selectedPrivateMapId: String? = null
    private var maps: List<MindMapQueryResponse> = emptyList()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onSearchTextChanged(text: String) {
        _uiState.updateUi { copy(searchText = text, content = (idle<SearchContentUi, Unit>()).takeIf { text.isEmpty() } ?: content) }

        loadMaps()
    }

    fun loadMaps() {
        searchJob?.cancel()
        val query = _uiState.ui.searchText
        if (query.isEmpty()) return

        searchJob = screenModelScope.launchCatching(
            catchBlock = {
                _uiState.updateUi { copy(content = Unit.error()) }
            }
        ) {
            _uiState.updateUi { copy(content = loading()) }

            maps = catalogInteractor.search(query)
            val uiElements = maps.map {
                MapQueryElement(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    isNeedPassword = it.isNeedPassword,
                )
            }
            delay(1_000L)
            _uiState.updateUi { copy(content = SearchContentUi(uiElements).success()) }
        }
    }

    fun onHidePasswordAlert() {
        privateMapAdd?.cancel()
        selectedPrivateMapId = null
        _uiState.updateUi { copy(enterPasswordAlert = null) }
    }

    fun onMapElementClick(mapId: String) {
        val clickedMap = maps.firstOrNull { it.id == mapId } ?: return
        if (clickedMap.isNeedPassword) {
            selectedPrivateMapId = mapId
            _uiState.updateUi { copy(enterPasswordAlert = EnterAlertModel()) }
        } else {
            addPublicMap(mapId)
        }
    }

    fun onPasswordEnter(password: String) {
        privateMapAdd?.cancel()
        val mapId = selectedPrivateMapId ?: return
        privateMapAdd = screenModelScope.launchCatching(
            catchBlock = {
                _uiState.showAlertError(UiError.Alert.Default(message = "Can't add private map"))
            },
            finalBlock = {
                _uiState.updateUi { copy(enterPasswordAlert = enterPasswordAlert?.copy(inProgress = false)) }
            }
        ) {
            _uiState.updateUi { copy(enterPasswordAlert = enterPasswordAlert?.copy(inProgress = true)) }

            delay(2_000L)
            catalogInteractor.addPrivateMap(mapId, password)
            _uiState.navigateTo(NavigationTarget.ToMapNavigate(MapScreenParams(id = mapId)))
        }
    }

    private fun addPublicMap(mapId: String) {
        screenModelScope.launchCatching(
            catchBlock = {
                _uiState.showAlertError(UiError.Alert.Default(message = "Can't add public map"))
            },
            finalBlock = {
                _uiState.updateUi {
                    copy(
                        searchFieldEnabled = true,
                        content = content.copy { successContent ->
                            successContent.copy(
                                maps = successContent.maps.map { it.copy(enabled = true, isLoading = false) }
                            )
                        }
                    )
                }
            }
        ) {
            _uiState.updateUi {
                copy(
                    searchFieldEnabled = false,
                    content = content.copy { successContent ->
                        successContent.copy(
                            maps = successContent.maps.map { it.copy(enabled = false, isLoading = it.id == mapId) }
                        )
                    }
                )
            }

            delay(2_000L)
            catalogInteractor.addPublicMap(mapId)

            _uiState.navigateTo(NavigationTarget.ToMapNavigate(MapScreenParams(id = mapId)))
        }
    }
}
