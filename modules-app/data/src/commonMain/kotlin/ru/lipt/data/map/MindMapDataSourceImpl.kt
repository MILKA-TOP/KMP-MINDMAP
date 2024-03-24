package ru.lipt.data.map

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import ru.lipt.core.device.ApplicationConfig
import ru.lipt.domain.map.MindMapDataSource
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.TestResultViewResponseRemote
import ru.lipt.domain.map.models.TestingCompleteRequestRemote
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote
import ru.lipt.domain.map.models.update.MapsUpdateRequestParams

class MindMapDataSourceImpl(
    private val config: ApplicationConfig,
    private val client: HttpClient,
) : MindMapDataSource {

    override suspend fun fetch(request: String): SummaryMapResponseRemote =
        client.get(
            urlString = "${config.baseUrl}/maps/fetch?mapId=$request"
        ).body()

//    override suspend fun createNewNode(mapId: String, parentId: String, title: String) = Node(
//        id = Random.nextInt().toString(),
//        parentId = parentId,
//        title = title,
//    )

    override suspend fun deleteMap(mapId: String) {
        client.post(
            urlString = "${config.baseUrl}/maps/delete?mapId=$mapId"
        )
    }

    override suspend fun eraseMap(mapId: String, type: MapRemoveType) {
        client.post(
            urlString = "${config.baseUrl}/maps/erase?mapId=$mapId&type=${type.name}"
        )
    }

    override suspend fun toggleNode(nodeId: String): Boolean =
        client.post(
            urlString = "${config.baseUrl}/nodes/toggle-selection?nodeId=$nodeId"
        ).body<NodeToggleResponseRemote>().isMarked

    override suspend fun updateMindMap(mapId: String, updateRequest: MapsUpdateRequestParams) {
        client.post(
            urlString = "${config.baseUrl}/maps/update?mapId=$mapId"
        ) {
            contentType(ContentType.Application.Json)
            setBody(updateRequest)
        }
    }

    override suspend fun sendTestAnswersForNode(testId: String, testAnswers: TestingCompleteRequestRemote): TestResultViewResponseRemote {
        return client.post(
            urlString = "${config.baseUrl}/tests/submit-test?testId=$testId"
        ) {
            contentType(ContentType.Application.Json)
            setBody(testAnswers)
        }.body()
    }

        @Serializable
        private data class NodeToggleResponseRemote(
            val nodeId: String,
            val isMarked: Boolean,
        )
}
