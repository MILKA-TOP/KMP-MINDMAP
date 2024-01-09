package ru.lipt.catalog.create.models

import androidx.compose.runtime.Immutable

@Immutable
data class CreateMindMapModel(
    val title: String = "",
    val description: String = "",
    val enabledPassword: Boolean = false,
    val password: String = "",
    val createButtonEnabled: Boolean = false,
)
