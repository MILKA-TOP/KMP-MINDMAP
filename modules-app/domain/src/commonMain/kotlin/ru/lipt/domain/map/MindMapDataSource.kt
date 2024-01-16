package ru.lipt.domain.map

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.map.models.MindMap
import ru.lipt.domain.map.models.Node
import ru.lipt.domain.map.models.QuestionResult
import ru.lipt.domain.map.models.RequestAnswer

interface MindMapDataSource : RemoteDataSource<String, MindMap> {
    suspend fun createNewNode(mapId: String, parentId: String, title: String): Node
    suspend fun deleteMap(mapId: String)
    suspend fun sendTestAnswersForNode(mapId: String, nodeId: String, answers: List<RequestAnswer>): QuestionResult
}
