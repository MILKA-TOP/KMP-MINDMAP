package ru.lipt.domain.map

import ru.lipt.core.cache.CachePolicyRepository
import ru.lipt.domain.map.models.MindMap

class MindMapRepository(
    private val localDataSource: MindMapLocalDataSource,
    private val remoteDataSource: MindMapDataSource
) : CachePolicyRepository<String, MindMap>(
    localDataSource = localDataSource,
    remoteDataSource = remoteDataSource,
) {
    suspend fun createNewNode(mapId: String, parentId: String, title: String) =
        remoteDataSource.createNewNode(mapId, parentId, title).also { newNode ->
            updateCache(mapId) {
                copy(nodes = nodes + newNode)
            }
        }

    suspend fun deleteMap(mapId: String) = remoteDataSource.deleteMap(mapId).also {
        localDataSource.remove(mapId)
    }
}
