package ru.lipt.catalog.main

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.catalog.main.models.CatalogScreenUi
import ru.lipt.catalog.main.models.CatalogScreenUi.Companion.toUi
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.domain.catalog.ICatalogInteractor
import ru.lipt.map.common.params.MapScreenParams

class CatalogScreenModel(
    private val catalogInteractor: ICatalogInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<CatalogScreenUi, NavigationTarget> =
        MutableScreenUiStateFlow(CatalogScreenUi())
    val uiState = _uiState.asStateFlow()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onStarted() = init()

    fun onMapElementClick(id: String) = _uiState.navigateTo(
        NavigationTarget.MapDestination(
            MapScreenParams(id)
        )
    )

    fun logout() {
        _uiState.navigateTo(NavigationTarget.EnterPinScreenDestination)
    }

    fun createNewMindMap() = _uiState.navigateTo(NavigationTarget.CreateMindMapDestination)
    fun searchMindMap() = _uiState.navigateTo(NavigationTarget.SearchMapDestination)

    private fun init() {
        screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi { copy(isLoadingInProgress = false) }
            }
        ) {
            _uiState.updateUi { copy(isLoadingInProgress = true) }
            val maps = catalogInteractor.getMaps()
            _uiState.updateUi {
                copy(
                    maps = maps.toUi()
                )
            }
        }
    }

    fun onPullToRefresh() {
        screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi { copy(isLoadingInProgress = false) }
            }
        ) {
            _uiState.updateUi { copy(isLoadingInProgress = true) }
            delay(REFRESH_DELAY)
            val maps = catalogInteractor.fetchMaps()
            _uiState.updateUi {
                copy(
                    maps = maps.toUi()
                )
            }
        }
    }

    fun onMigrateButtonClick() {
        _uiState.navigateTo(NavigationTarget.MigrateMapDestination)
    }

    companion object {
        private const val REFRESH_DELAY = 250L
    }
}
