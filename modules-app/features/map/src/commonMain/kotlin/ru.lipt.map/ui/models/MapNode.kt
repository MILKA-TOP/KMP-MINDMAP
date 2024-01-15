package ru.lipt.map.ui.models

import androidx.compose.runtime.Immutable
import ru.lipt.domain.map.models.Node

@Immutable
data class MapNode(
    val id: String,
    val text: String,
    val position: NodePosition = NodePosition(),
    val parentId: String? = null
) {
    companion object {
        fun Node.toUi() = MapNode(
            id = id,
            text = title,
            parentId = parentId
        )
    }
}
