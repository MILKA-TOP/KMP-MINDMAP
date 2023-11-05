package ru.lipt.map.ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class MapNode(
    val id: String,
    val text: String,
    val position: NodePosition = NodePosition(),
    val childIds: List<String> = listOf()
)
