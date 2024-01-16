package ru.lipt.domain.map

import ru.lipt.core.cache.CachePolicyRepository
import ru.lipt.domain.map.models.MindMap
import ru.lipt.domain.map.models.RequestAnswer

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

    suspend fun sendTestAnswersForNode(mapId: String, nodeId: String, answers: List<RequestAnswer>) =
        remoteDataSource.sendTestAnswersForNode(mapId, nodeId, answers).also { result ->
            updateCache(mapId) {
                copy(
                    nodes = nodes.map { if (it.id == nodeId) it.copy(result = result) else it }
                )
            }
        }
}
