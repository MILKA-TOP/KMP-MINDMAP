package ru.lipt.catalog.ui

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.catalog.ui.models.CatalogScreenUi
import ru.lipt.catalog.ui.models.MapCatalogElement

class CatalogScreenModel : ScreenModel {

    private val _uiState: MutableStateFlow<CatalogScreenUi> = MutableStateFlow(
        CatalogScreenUi(
            maps = listOf(
                MapCatalogElement(
                    id = "0",
                    title = "Some title 1",
                    description = "Some description 1"
                ),
                MapCatalogElement(
                    id = "1",
                    title = "Some title 2",
                    description = "Some description 2"
                )
            )
        )
    )
    val uiState = _uiState.asStateFlow()
}
