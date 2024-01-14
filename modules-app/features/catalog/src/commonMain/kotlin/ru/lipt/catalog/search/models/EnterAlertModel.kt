package ru.lipt.catalog.search.models

import androidx.compose.runtime.Immutable

@Immutable
data class EnterAlertModel(
    val inProgress: Boolean = false,
)
