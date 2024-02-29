package ru.lipt.domain.map

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote

interface MindMapDataSource : RemoteDataSource<String, SummaryMapResponseRemote> {
    suspend fun deleteMap(mapId: String)
    suspend fun eraseMap(mapId: String, type: MapRemoveType)
    suspend fun toggleNode(nodeId: String): Boolean
//    suspend fun sendTestAnswersForNode(mapId: String, nodeId: String, answers: List<RequestAnswer>): QuestionResult
}
