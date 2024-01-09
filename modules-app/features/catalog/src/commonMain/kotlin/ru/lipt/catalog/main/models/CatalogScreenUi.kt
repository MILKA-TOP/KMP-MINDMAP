package ru.lipt.catalog.main.models

import androidx.compose.runtime.Immutable

@Immutable
data class CatalogScreenUi(
    val maps: List<MapCatalogElement> = listOf()
)
