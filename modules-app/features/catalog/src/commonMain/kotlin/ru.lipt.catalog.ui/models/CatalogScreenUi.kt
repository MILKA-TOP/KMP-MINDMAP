package ru.lipt.catalog.ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class CatalogScreenUi(
    val maps: List<MapCatalogElement> = listOf()
)
