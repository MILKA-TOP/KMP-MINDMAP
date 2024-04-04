package ru.lipt.domain.map

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.TestResultViewResponseRemote
import ru.lipt.domain.map.models.TestingCompleteRequestRemote
import ru.lipt.domain.map.models.TestsEditResponseRemote
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote
import ru.lipt.domain.map.models.update.MapsUpdateRequestParams

interface MindMapDataSource : RemoteDataSource<String, SummaryMapResponseRemote> {
    suspend fun deleteMap(mapId: String)
    suspend fun eraseMap(mapId: String, type: MapRemoveType)
    suspend fun toggleNode(nodeId: String): Boolean
    suspend fun updateMindMap(mapId: String, updateRequest: MapsUpdateRequestParams)
    suspend fun sendTestAnswersForNode(testId: String, testAnswers: TestingCompleteRequestRemote): TestResultViewResponseRemote
    suspend fun generateTest(nodeId: String): TestsEditResponseRemote
}
