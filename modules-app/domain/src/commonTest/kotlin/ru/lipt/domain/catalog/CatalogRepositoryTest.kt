package ru.lipt.domain.catalog

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Mock
import org.kodein.mock.UsesFakes
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.domain.catalog.models.CatalogMindMap
import ru.lipt.domain.catalog.models.fakeCatalogMindMap
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("TooGenericExceptionThrown")
@UsesFakes(CatalogMindMap::class)
class CatalogRepositoryTest : TestsWithMocks() {

    @Mock
    lateinit var catalogRemoteDataSource: CatalogDataSource
    private val catalogLocalDataSource: CatalogLocalDataSource = CatalogLocalDataSource()
    private val repository: CatalogRepository by withMocks { CatalogRepository(catalogLocalDataSource, catalogRemoteDataSource) }
    override fun setUpMocks() = injectMocks(mocker)

    @Test
    fun `createMap calls remoteDataSource with provided parameters and triggers cache refresh`() = runTest {
        val title = "Test Map"
        val description = "A test map description"
        val password = "secret"
        val mapRefId = "refId123"
        everySuspending { catalogRemoteDataSource.fetch(Unit) } returns emptyList()
        everySuspending { catalogRemoteDataSource.createMap(title, description, password, mapRefId) } returns "mapId"

        val result = repository.createMap(title, description, password, mapRefId)

        verifyWithSuspend(exhaustive = false) { catalogRemoteDataSource.createMap(title, description, password, mapRefId) }
        assertEquals("mapId", result)
        verifyWithSuspend(exhaustive = false) { catalogLocalDataSource.set(Unit, isAny()) }
    }

    @Test
    fun `search calls remoteDataSource with query and returns its result`() = runTest {
        val query = "Kotlin"
        everySuspending { catalogRemoteDataSource.fetch(Unit) } returns emptyList()
        everySuspending { catalogRemoteDataSource.search(query) } returns listOf(fakeCatalogMindMap())

        val result = repository.search(query)

        verifyWithSuspend { catalogRemoteDataSource.search(query) }
        assertEquals(1, result.size)
        assertEquals("", result.first().title)
    }

    @Test
    fun `migrate calls remoteDataSource with provided parameters and triggers cache refresh`() = runTest {
        val text = "map data"
        val password = "password123"
        everySuspending { catalogRemoteDataSource.fetch(Unit) } returns emptyList()
        everySuspending { catalogRemoteDataSource.migrate(text, password, MigrateType.MINDOMO_TEXT) } returns "migratedMapId"

        val result = repository.migrate(text, password)

        verifyWithSuspend(exhaustive = false) { catalogRemoteDataSource.migrate(text, password, MigrateType.MINDOMO_TEXT) }
        assertEquals("migratedMapId", result)
        verifyWithSuspend(exhaustive = false) { catalogLocalDataSource.set(Unit, isAny()) }
    }

    @Test
    fun `addMap calls remoteDataSource with mapId and password`() = runTest {
        val mapId = "mapId123"
        val password = "password123"
        everySuspending { catalogRemoteDataSource.addMap(mapId, password) } returns Unit

        repository.addMap(mapId, password)

        verifyWithSuspend { catalogRemoteDataSource.addMap(mapId, password) }
    }
}
