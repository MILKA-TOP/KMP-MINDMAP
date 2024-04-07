package ru.lipt.domain.map

import ru.lipt.core.cache.CachePolicy
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.NodesEditResponseRemote
import ru.lipt.domain.map.models.NodesViewResponseRemote
import ru.lipt.domain.map.models.QuestionsEditResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.TestResultViewResponseRemote
import ru.lipt.domain.map.models.TestingCompleteRequestRemote
import ru.lipt.domain.map.models.TestsEditResponseRemote
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote

interface IMindMapInteractor {
    suspend fun getMap(id: String, cachePolicy: CachePolicy = CachePolicy.ALWAYS): SummaryMapResponseRemote

    suspend fun fetchViewMap(mapId: String, userId: String, cachePolicy: CachePolicy = CachePolicy.ALWAYS): SummaryViewMapResponseRemote

    suspend fun addNewNodeToMap(mapId: String, parentId: String, title: String): SummaryMapResponseRemote

    suspend fun getEditableNode(mapId: String, nodeId: String): NodesEditResponseRemote

    suspend fun getViewNode(mapId: String, nodeId: String): NodesViewResponseRemote

    suspend fun deleteMap(mapId: String)

    suspend fun eraseMap(mapId: String, type: MapRemoveType)

    suspend fun updateMindMap(mapId: String): SummaryMapResponseRemote

    suspend fun updateNodePosition(mapId: String, nodeId: String, index: Int): SummaryMapResponseRemote

    suspend fun saveTitleAndData(mapId: String, title: String, description: String)

    suspend fun toggleNode(mapId: String, nodeId: String): Boolean

    suspend fun saveNodeData(mapId: String, nodeId: String, title: String, description: String)

    suspend fun removeNode(mapId: String, nodeId: String)

    suspend fun updateQuestions(mapId: String, nodeId: String, testId: String, questions: List<QuestionsEditResponseRemote>)

    suspend fun sendTestAnswersForNode(
        mapId: String,
        nodeId: String,
        testId: String,
        testAnswers: TestingCompleteRequestRemote
    ): TestResultViewResponseRemote

    suspend fun generateTest(mapId: String, nodeId: String): TestsEditResponseRemote
}
