package ru.lipt.map.ui.common

import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.map.ui.models.MindMapBox
import ru.lipt.map.ui.models.MindMapColumn
import ru.lipt.map.ui.models.MindMapNodeVertex
import ru.lipt.map.ui.models.ViewMapNode

fun SummaryViewMapResponseRemote.toViewBoxUi(): MindMapBox {
        val rootNode = this.nodes.first { it.parentNodeId == null }
        val rootColumn = MindMapColumn(
            listOf(
                MindMapNodeVertex(
                    nodes = listOf(
                        ViewMapNode(
                            nodeId = rootNode.id, title = title, priorityPosition = 0, isMarked = rootNode.isSelected
                        )
                    )
                )
            )
        )
        val columns = mutableListOf(rootColumn)

        var i = 0
        while (true) {
            val previousColumn = columns[i]
            val currentGroups = mutableListOf<MindMapNodeVertex>()
            previousColumn.groups.map { group ->
                group.nodes.map { parentNode ->
                    val children = nodes.filter { it.parentNodeId == parentNode.nodeId }.sortedBy { it.priorityPosition }
                    if (children.isNotEmpty()) {
                        currentGroups.add(MindMapNodeVertex(parentNodeId = parentNode.nodeId,
                            nodes = children.sortedBy { it.priorityPosition }.map {
                                ViewMapNode(
                                    nodeId = it.id,
                                    title = it.label,
                                    parentNodeId = parentNode.nodeId,
                                    priorityPosition = it.priorityPosition,
                                    isMarked = it.isSelected,
                                )
                            })
                        )
                    }
                }
            }
            if (currentGroups.isEmpty()) break

            i++
            columns.add(MindMapColumn(groups = currentGroups))
        }
        return MindMapBox(columns = columns)
    }
