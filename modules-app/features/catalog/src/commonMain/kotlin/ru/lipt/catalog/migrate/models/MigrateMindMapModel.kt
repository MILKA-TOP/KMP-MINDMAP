package ru.lipt.catalog.migrate.models

import androidx.compose.runtime.Immutable

@Immutable
data class MigrateMindMapModel(
    val showFilePicker: Boolean = false,
    val selectedFileTitle: String? = null,
    val enabledPassword: Boolean = false,
    val password: String = "",
    val createButtonEnabled: Boolean = false,
    val buttonInProgress: Boolean = false,
)
