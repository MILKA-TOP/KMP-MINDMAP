package ru.lipt.domain.map

import ru.lipt.core.cache.CachePolicyRepository
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote

class MindMapRepository(
    private val localDataSource: MindMapLocalDataSource, private val remoteDataSource: MindMapDataSource
) : CachePolicyRepository<String, SummaryMapResponseRemote>(
    localDataSource = localDataSource,
    remoteDataSource = remoteDataSource,
) {
//    suspend fun createNewNode(mapId: String, parentId: String, title: String) = remoteDataSource.createNewNode(mapId, parentId, title)

    suspend fun deleteMap(mapId: String) = remoteDataSource.deleteMap(mapId).also {
        localDataSource.remove(mapId)
    }

    suspend fun eraseMap(mapId: String, type: MapRemoveType) = remoteDataSource.eraseMap(mapId, type).also {
        localDataSource.remove(mapId)
    }

    suspend fun toggleNode(mapId: String, nodeId: String): Boolean = remoteDataSource.toggleNode(nodeId).also { bool ->
        updateCache(mapId) {
            val viewMap = this as? SummaryViewMapResponseRemote ?: return@updateCache this
            viewMap.copy(
                nodes = viewMap.nodes.map { if (it.id == nodeId) it.copy(isSelected = bool) else it }
            )
        }
    }

//    suspend fun sendTestAnswersForNode(mapId: String, nodeId: String, answers: List<RequestAnswer>) =
//        remoteDataSource.sendTestAnswersForNode(mapId, nodeId, answers).also { result ->
//            updateCache(mapId) {
//                copy(
//                    nodes = nodes.map { if (it.id == nodeId) it.copy(result = result) else it }
//                )
//            }
//        }
}
