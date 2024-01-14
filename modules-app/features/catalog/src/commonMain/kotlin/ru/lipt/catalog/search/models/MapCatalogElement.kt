package ru.lipt.catalog.search.models

import androidx.compose.runtime.Immutable

@Immutable
data class MapQueryElement(
    val id: String,
    val title: String,
    val description: String,
    val isNeedPassword: Boolean,
    val enabled: Boolean = true,
    val isLoading: Boolean = false,
)
