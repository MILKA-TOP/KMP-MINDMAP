package ru.lipt.domain.map

import ru.lipt.core.cache.CachePolicy
import ru.lipt.domain.catalog.CatalogRepository
import ru.lipt.domain.map.models.RequestAnswer

class MindMapInteractor(
    private val mapRepository: MindMapRepository,
    private val catalogRepository: CatalogRepository,
) {
    suspend fun getMap(id: String) = mapRepository.fetch(id, CachePolicy.ALWAYS)!!

    suspend fun createNewNode(mapId: String, parentId: String, title: String) = mapRepository.createNewNode(mapId, parentId, title)

    suspend fun getNode(mapId: String, nodeId: String) = getMap(mapId).nodes.first { it.id == nodeId }

    suspend fun deleteMap(mapId: String) = mapRepository.deleteMap(mapId).also {
        catalogRepository.removeMap(mapId)
    }

    suspend fun sendTestAnswersForNode(mapId: String, nodeId: String, answers: List<RequestAnswer>) =
        mapRepository.sendTestAnswersForNode(mapId, nodeId, answers)
}
