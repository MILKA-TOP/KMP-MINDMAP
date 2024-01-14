package ru.lipt.catalog.models

import androidx.compose.runtime.Immutable

@Immutable
data class MapCatalogElement(
    val id: String,
    val title: String,
    val description: String,
)
