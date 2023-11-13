package ru.lipt.catalog.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.lipt.catalog.ui.models.CatalogScreenUi
import ru.lipt.catalog.ui.models.MapCatalogElement
import ru.lipt.domain.catalog.CatalogInteractor

class CatalogScreenModel(
    val catalogInteractor: CatalogInteractor,
) : ScreenModel {

    private val _uiState: MutableStateFlow<CatalogScreenUi> = MutableStateFlow(CatalogScreenUi())
    val uiState = _uiState.asStateFlow()

    init {
        init()
    }

    private fun init() {
        screenModelScope.launch {
            val maps = catalogInteractor.getMaps()
            _uiState.update {
                it.copy(
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
