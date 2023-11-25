package ru.lipt.core.compose

import androidx.compose.runtime.Immutable

@Immutable
data class UiState<Ui, Nav>(
    val model: Ui,
    val navigationEvents: List<Nav> = emptyList(),
)
