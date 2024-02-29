package ru.lipt.map.ui.models

import androidx.compose.runtime.Immutable

@Immutable
@Suppress("UnnecessaryAbstractClass")
abstract class MapNode(
    open val nodeId: String,
    open val title: String,
    open val priorityPosition: Int,
    open val parentNodeId: String? = null,
)

@Immutable
data class EditMapNode(
    override val nodeId: String,
    override val title: String,
    override val priorityPosition: Int,
    override val parentNodeId: String? = null,
) : MapNode(
    nodeId = nodeId,
    title = title,
    priorityPosition = priorityPosition,
    parentNodeId = parentNodeId
)

@Immutable
data class ViewMapNode(
    override val nodeId: String,
    override val title: String,
    override val priorityPosition: Int,
    override val parentNodeId: String? = null,
    val isMarked: Boolean,
) : MapNode(
    nodeId = nodeId,
    title = title,
    priorityPosition = priorityPosition,
    parentNodeId = parentNodeId
)
