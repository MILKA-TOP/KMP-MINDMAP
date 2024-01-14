package ru.lipt.catalog.main

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.lipt.catalog.main.models.CatalogScreenUi
import ru.lipt.catalog.main.models.CatalogScreenUi.Companion.toUi
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.domain.catalog.CatalogInteractor
import ru.lipt.domain.login.LoginInteractor
import ru.lipt.map.common.params.MapScreenParams

class CatalogScreenModel(
    private val catalogInteractor: CatalogInteractor,
    private val loginInteractor: LoginInteractor,
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
        screenModelScope.launchCatching(
            catchBlock = {
                _uiState.showAlertError(
                    UiError.Alert.Default(
                        message = "Не удалось выйти"
                    )
                )
            }
        ) {
            loginInteractor.logout()
            _uiState.navigateTo(NavigationTarget.HelloScreenDestination)
        }
    }

    fun createNewMindMap() = _uiState.navigateTo(NavigationTarget.CreateMindMapDestination)
    fun searchMindMap() = _uiState.navigateTo(NavigationTarget.SearchMapDestination)

    private fun init() {
        screenModelScope.launch {
            val maps = catalogInteractor.getMaps()
            _uiState.updateUi {
                copy(
                    maps = maps.toUi()
                )
            }
        }
    }
}
