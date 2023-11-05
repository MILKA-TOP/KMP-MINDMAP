package ru.lipt.map.ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class MapEdge(
    val firstNodeId: String,
    val secondNodeId: String,
    val firstPosition: NodePosition = NodePosition(),
    val secondPosition: NodePosition = NodePosition(),
) {
    val middlePosition: NodePosition = NodePosition(
        x = (firstPosition.x + secondPosition.x) / 2,
        y = (firstPosition.y + secondPosition.y) / 2,
    )
}

@Immutable
data class NodePosition(
    val x: Float = 0f, val y: Float = 0f
)
