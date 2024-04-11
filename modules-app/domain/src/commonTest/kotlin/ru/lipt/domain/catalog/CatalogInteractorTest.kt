package ru.lipt.domain.catalog

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.kodein.mock.Fake
import org.kodein.mock.Mock
import org.kodein.mock.UsesFakes
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.core.cache.CachePolicy
import ru.lipt.domain.catalog.models.CatalogMindMap
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("TooGenericExceptionThrown")
@UsesFakes(CachePolicy::class)
class CatalogInteractorTest : TestsWithMocks() {

    @Mock
    lateinit var catalogRemoteDataSource: CatalogDataSource

    @Fake
    lateinit var catalogLocalDataSource: CatalogLocalDataSource
    private val catalogRepository: CatalogRepository by withMocks { CatalogRepository(catalogLocalDataSource, catalogRemoteDataSource) }

    private val interactor: CatalogInteractor by withMocks { CatalogInteractor(catalogRepository) }
    override fun setUpMocks() = injectMocks(mocker)

    @Test
    fun `getMaps calls repository with correct cache policy and returns its result`() = runTest {
        val expectedMaps = listOf<CatalogMindMap>()
        everySuspending { catalogRemoteDataSource.fetch(isAny()) } returns expectedMaps

        val result = interactor.getMaps()
        advanceUntilIdle()
        verifyWithSuspend { catalogRepository.fetch(isAny(), CachePolicy.ALWAYS) }

        assertEquals(expectedMaps, result)
    }

    @Test
    fun `fetchMaps calls repository to refresh data and returns its result`() = runTest {
        val expectedMaps = listOf<CatalogMindMap>()
        everySuspending { catalogRemoteDataSource.fetch(isEqual(Unit)) } returns expectedMaps

        val result = interactor.fetchMaps()
        verifyWithSuspend { catalogRepository.fetch(isAny(), CachePolicy.REFRESH) }

        assertEquals(expectedMaps, result)
    }

    @Test
    fun `createMap calls repository with provided parameters and returns its result`() = runTest {
        val title = "Test Map"
        val description = "Test Description"
        val password = "Test Password"
        val mapRefId = "RefId"
        val expectedMapId = "MapId"
        everySuspending { catalogRepository.createMap(title, description, password, mapRefId) } returns expectedMapId

        val result = interactor.createMap(title, description, password, mapRefId)
        verifyWithSuspend { catalogRepository.createMap(isEqual(title), isEqual(description), isEqual(password), isEqual(mapRefId)) }

        assertEquals(expectedMapId, result)
    }

    @Test
    fun `migrate calls repository with provided parameters and returns its result`() = runTest {
        val text = "Map Data"
        val password = "Test Password"
        val expectedMigrationId = "MigrationId"
        everySuspending {
            catalogRepository.migrate(
                isEqual(text),
                isEqual(password),
                isEqual(MigrateType.MINDOMO_TEXT)
            )
        } returns expectedMigrationId

        val result = interactor.migrate(text, password, MigrateType.MINDOMO_TEXT)
        verifyWithSuspend { catalogRepository.migrate(text, password, MigrateType.MINDOMO_TEXT) }

        assertEquals(expectedMigrationId, result)
    }

    @Test
    fun `search calls repository with provided query and returns its result`() = runTest {
        val query = "Test Query"
        val expectedSearchResults = listOf<CatalogMindMap>()
        everySuspending { catalogRepository.search(isEqual(query)) } returns expectedSearchResults

        val result = interactor.search(query)
        verifyWithSuspend { catalogRepository.search(isEqual(query)) }

        assertEquals(expectedSearchResults, result)
    }

    @Test
    fun `addMap calls repository with provided parameters and attempts to refresh maps`() = runTest {
        val mapId = "MapId"
        val password = "Password"
        everySuspending { catalogRemoteDataSource.addMap(isEqual(mapId), isEqual(password)) } returns Unit
        everySuspending { catalogRemoteDataSource.fetch(isEqual(Unit)) } returns listOf()

        interactor.addMap(mapId, password)
        advanceUntilIdle()
    }
}
