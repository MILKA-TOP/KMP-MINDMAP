package ru.lipt.map.ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class MapScreenUi(
    val title: String = "",
    val box: MindMapBox = MindMapBox(),
    val updateInProgress: Boolean = false,
    val isSaveButtonVisible: Boolean = false,
    val alert: MapScreenUi.EnterNewNodeTitle? = null,
) {
    data class EnterNewNodeTitle(val title: String = "", val inProgress: Boolean = false) {
        val isConfirmButtonEnabled = title.isNotEmpty()
    }
}

@Immutable
data class MapViewScreenUi(
    val title: String = "",
    val box: MindMapBox = MindMapBox(),
)

@Immutable
data class MindMapNodeVertex(
    val parentNodeId: String? = null,
    val nodes: List<MapNode> = emptyList()
)

@Immutable
data class MindMapColumn(
    val groups: List<MindMapNodeVertex> = emptyList()
)

@Immutable
data class MindMapBox(
    val columns: List<MindMapColumn> = emptyList(),
    val groupsDraggableParentId: String? = null,
)
