package ru.lipt.map.ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class MapScreenUi(
    val nodes: Map<String, MapNode> = mapOf(),
    val edges: List<MapEdge> = listOf(),
    val newNodeAlert: NewNodeAlertModel? = null,
)
