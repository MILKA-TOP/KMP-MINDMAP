package ru.lipt.data.catalog

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import ru.lipt.core.device.ApplicationConfig
import ru.lipt.domain.catalog.CatalogDataSource
import ru.lipt.domain.catalog.MigrateType
import ru.lipt.domain.catalog.models.CatalogMindMap

class CatalogDataSourceImpl(
    private val client: HttpClient,
    private val config: ApplicationConfig,
) : CatalogDataSource {

    override suspend fun createMap(title: String, description: String, password: String?, mapRefId: String?): String =
        client.post(
            urlString = "${config.baseUrl}/maps/create"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                MapsCreateRequestParams(
                    title = title,
                    description = description,
                    password = password,
                    ref = mapRefId,
                )
            )
        }.body<MapIdResponseRemote>().mapId

    override suspend fun search(query: String): List<CatalogMindMap> =
        client.get(
            urlString = "${config.baseUrl}/catalog/search?query=$query"
        ).body()

    override suspend fun addMap(mapId: String, password: String?) {
        client.post(
            urlString = "${config.baseUrl}/maps/add-map"
        ) {
            contentType(ContentType.Application.Json)
            setBody(AddMapRequest(mapId, password))
        }
    }

    override suspend fun addPrivateMap(mapId: String, password: String) = Unit
    override suspend fun migrate(text: String, password: String?, type: MigrateType): String = client.post(
        urlString = "${config.baseUrl}/maps/migrate"
    ) {
        contentType(ContentType.Application.Json)
        setBody(MapsMigrateRequestParams(text, type, password))
    }.body<MapIdResponseRemote>().mapId

    override suspend fun fetch(request: Unit): List<CatalogMindMap> =
        client.get(
            urlString = "${config.baseUrl}/catalog"
        ).body()

    @Serializable
    private data class AddMapRequest(val mapId: String, val password: String? = null)

    @Serializable
    private data class MapsCreateRequestParams(
        val title: String,
        val description: String,
        val password: String? = null,
        val ref: String? = null,
    )

    @Serializable
    private data class MapsMigrateRequestParams(
        val text: String,
        val type: MigrateType,
        val password: String? = null,
    )

    @Serializable
    private data class MapIdResponseRemote(
        val mapId: String
    )
}
