package ru.lipt.domain.map

import ru.lipt.core.cache.CachePolicy
import ru.lipt.core.uuid.randomUUID
import ru.lipt.domain.catalog.CatalogRepository
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.NodesEditResponseRemote
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote

class MindMapInteractor(
    private val mapRepository: MindMapRepository,
    private val catalogRepository: CatalogRepository,
) {
    suspend fun getMap(id: String) = mapRepository.fetch(id, CachePolicy.ALWAYS)!!
    suspend fun addNewNodeToMap(mapId: String, parentId: String, title: String) = mapRepository.updateCache(mapId) {
        (this as? SummaryEditMapResponseRemote)?.let {
            val catalogSection = it.nodes.filter { it.parentNodeId == parentId }
            it.copy(
                nodes = nodes + NodesEditResponseRemote(
                    id = randomUUID(), label = title, priorityPosition = catalogSection.size + 1, parentNodeId = parentId
                )
            )
        } ?: this
    }.let { getMap(mapId) }

    suspend fun getEditableNode(mapId: String, nodeId: String) =
        (getMap(mapId) as SummaryEditMapResponseRemote).nodes.first { it.id == nodeId }

    suspend fun getViewNode(mapId: String, nodeId: String) =
        (getMap(mapId) as SummaryViewMapResponseRemote).nodes.first { it.id == nodeId }

//    suspend fun createNewNode(mapId: String, parentId: String, title: String) = mapRepository.createNewNode(mapId, parentId, title)

//    suspend fun getNode(mapId: String, nodeId: String) = getMap(mapId).nodes.first { it.id == nodeId }

    suspend fun deleteMap(mapId: String) = mapRepository.deleteMap(mapId).also {
        catalogRepository.removeMap(mapId)
    }

    suspend fun eraseMap(mapId: String, type: MapRemoveType) = mapRepository.eraseMap(mapId, type).also {
        catalogRepository.removeMap(mapId)
    }

    suspend fun updateNodePosition(mapId: String, nodeId: String, index: Int) = mapRepository.updateCache(mapId) {
        (this as? SummaryEditMapResponseRemote)?.let { map ->
            val parentNodeId = map.nodes.first { it.id == nodeId }.parentNodeId
            val catalogSection = map.nodes.filter { it.parentNodeId == parentNodeId }.sortedBy { it.priorityPosition }.map {
                when {
                    it.id == nodeId -> it.copy(priorityPosition = index)
                    it.priorityPosition < index -> it
                    else -> it.copy(priorityPosition = it.priorityPosition + 1)
                }
            }.associateBy { it.id }
            map.copy(nodes = nodes.map {
                if (it.id in catalogSection) catalogSection[it.id]!! else it
            })
        } ?: this
    }.let { getMap(mapId) }

    suspend fun saveTitleAndData(mapId: String, title: String, description: String) = mapRepository.updateCache(
        mapId
    ) {
        (this as? SummaryEditMapResponseRemote)?.copy(title = title, description = description) ?: this
    }

    suspend fun toggleNode(mapId: String, nodeId: String) = mapRepository.toggleNode(mapId, nodeId)

    suspend fun saveNodeData(
        mapId: String, nodeId: String, title: String, description: String
    ) = mapRepository.updateCache(mapId) {
        (this as? SummaryEditMapResponseRemote)?.let { map ->
            copy(nodes = nodes.map { node ->
                if (node.id == nodeId) node.copy(
                    label = title,
                    description = description,
                ) else node
            })
        } ?: this
    }

    suspend fun removeNode(mapId: String, nodeId: String) = mapRepository.updateCache(mapId) {
        (this as? SummaryEditMapResponseRemote)?.let { map ->
            val currentNode = map.nodes.first { it.id == nodeId }
            val parentNodeId = currentNode.parentNodeId
            copy(nodes = nodes.filter { it.id != nodeId }.map { node ->
                if (node.parentNodeId == nodeId) node.copy(parentNodeId = parentNodeId) else node
            })
        } ?: this
    }

//    suspend fun sendTestAnswersForNode(mapId: String, nodeId: String, answers: List<RequestAnswer>) =
//        mapRepository.sendTestAnswersForNode(mapId, nodeId, answers)
}
