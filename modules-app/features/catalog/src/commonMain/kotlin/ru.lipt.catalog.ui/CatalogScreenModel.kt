package ru.lipt.catalog.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.lipt.catalog.ui.models.CatalogScreenUi
import ru.lipt.catalog.ui.models.MapCatalogElement
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.domain.catalog.CatalogInteractor
import ru.lipt.map.common.params.MapScreenParams

class CatalogScreenModel(
    val catalogInteractor: CatalogInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<CatalogScreenUi, NavigationTarget> =
        MutableScreenUiStateFlow(CatalogScreenUi())
    val uiState = _uiState.asStateFlow()

    init {
        init()
    }

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    fun onMapElementClick(id: String) = _uiState.navigateTo(NavigationTarget.MapDestination(
        MapScreenParams(id)
    ))

    private fun init() {
        screenModelScope.launch {
            val maps = catalogInteractor.getMaps()
            _uiState.updateUi {
                copy(
                    maps = maps.map { map ->
                        MapCatalogElement(
                            id = map.id,
                            title = map.title,
                            description = map.description,
                        )
                    }
                )
            }
        }
    }
}
