package ru.lipt.domain.map

import ru.lipt.core.cache.CachePolicy

class MindMapInteractor(
    private val mapRepository: MindMapRepository,
) {
    suspend fun getMap(id: String) = mapRepository.fetch(id, CachePolicy.ALWAYS)!!

    suspend fun createNewNode(mapId: String, parentId: String, title: String) = mapRepository.createNewNode(mapId, parentId, title)

    suspend fun getNode(mapId: String, nodeId: String) = getMap(mapId).nodes.first { it.id == nodeId }
}
