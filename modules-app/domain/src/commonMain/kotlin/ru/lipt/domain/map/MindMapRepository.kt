package ru.lipt.domain.map

import ru.lipt.core.cache.CacheEntry
import ru.lipt.core.cache.CachePolicyRepository
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.TestResultViewResponseRemote
import ru.lipt.domain.map.models.TestingCompleteRequestRemote
import ru.lipt.domain.map.models.TestsEditResponseRemote
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote
import ru.lipt.domain.map.models.update.MapsUpdateRequestParams

class MindMapRepository(
    private val updateDataSource: MindMapUpdateLocalDataSource,
    private val localDataSource: MindMapLocalDataSource, private val remoteDataSource: MindMapDataSource
) : CachePolicyRepository<String, SummaryMapResponseRemote>(
    localDataSource = localDataSource,
    remoteDataSource = remoteDataSource,
) {
    suspend fun deleteMap(mapId: String) = remoteDataSource.deleteMap(mapId).also {
        localDataSource.remove(mapId)
        updateDataSource.remove(mapId)
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

    suspend fun updateRequestCache(
        mapId: String,
        update: MapsUpdateRequestParams.(map: SummaryEditMapResponseRemote) -> MapsUpdateRequestParams
    ) {
        val mapCache = localDataSource.get(mapId) ?: throw IllegalStateException("Nothing to update in cache!")
        val map = mapCache.value as SummaryEditMapResponseRemote
        val oldValue: MapsUpdateRequestParams =
            updateDataSource.get(mapId)?.value ?: MapsUpdateRequestParams(title = map.title, description = map.description)
        updateDataSource.set(mapId, CacheEntry(key = mapId, value = oldValue.update(map)))
    }

    suspend fun updateMindMap(mapId: String) {
        val updateParams = updateDataSource.get(mapId)?.value ?: return
        remoteDataSource.updateMindMap(mapId, updateParams)
        updateDataSource.remove(mapId)
    }
    suspend fun sendTestAnswersForNode(testId: String, testAnswers: TestingCompleteRequestRemote): TestResultViewResponseRemote =
        remoteDataSource.sendTestAnswersForNode(testId, testAnswers)

    suspend fun generateTest(nodeId: String): TestsEditResponseRemote = remoteDataSource.generateTest(nodeId)
}
