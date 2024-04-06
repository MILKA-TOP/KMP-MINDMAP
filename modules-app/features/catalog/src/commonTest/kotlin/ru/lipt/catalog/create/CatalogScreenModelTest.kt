import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.catalog.main.CatalogScreenModel
import ru.lipt.catalog.main.NavigationTarget
import ru.lipt.catalog.models.MapCatalogElement
import ru.lipt.domain.catalog.ICatalogInteractor
import ru.lipt.domain.catalog.models.CatalogMindMap
import ru.lipt.domain.catalog.models.UserDomainModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    @Mock
    lateinit var catalogInteractor: ICatalogInteractor

    private val model by withMocks { CatalogScreenModel(catalogInteractor) }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `initial state is correct`() = runTest {
        with(model.uiState.value.model) {
            assertTrue(maps.isEmpty())
            assertFalse(isLoadingInProgress)
        }
    }

    @Test
    fun `onStarted initiates loading and fetches maps base`() = runTest {
        val inputMaps = listOf<CatalogMindMap>(
            CatalogMindMap(
                "1", "Mind Map 1", UserDomainModel("adminId", "admin@example.com"),
                "Description 1", true, true, false
            )
        )
        val outputMaps = listOf(
            MapCatalogElement(
                id = "1",
                title = "Mind Map 1",
                adminEmail = "admin@example.com",
                description = "Description 1",
                type = MapCatalogElement.MapType.EDITABLE,
                isPrivate = false,
                isEnabledEdit = true,
                isSaved = true
            )
        )
        everySuspending { catalogInteractor.getMaps() } returns inputMaps

        model.onStarted()
        advanceTimeBy(500) // Adjust based on your coroutine setup

        with(model.uiState.value.model) {
            assertEquals(maps, outputMaps)
            assertFalse(isLoadingInProgress)
        }
    }
    @Test
    fun `onStarted initiates loading and fetches maps empty`() = runTest {
        val inputMaps = listOf<CatalogMindMap>()
        val outputMaps = listOf<MapCatalogElement>()
        everySuspending { catalogInteractor.getMaps() } returns inputMaps

        model.onStarted()
        advanceTimeBy(500) // Adjust based on your coroutine setup

        with(model.uiState.value.model) {
            assertEquals(maps, outputMaps)
            assertFalse(isLoadingInProgress)
        }
    }

    @Test
    fun `onPullToRefresh refreshes maps base and updates state`() = runTest {
        val refreshedMaps = listOf(
            CatalogMindMap(
                "1", "Mind Map 1", UserDomainModel("adminId", "admin@example.com"),
                "Description of Map 1", true, true, false
            )
        )
        val outputMaps = listOf(
            MapCatalogElement(
                id = "1",
                title = "Mind Map 1",
                adminEmail = "admin@example.com",
                description = "Description of Map 1",
                type = MapCatalogElement.MapType.EDITABLE,
                isPrivate = false,
                isEnabledEdit = true,
                isSaved = true
            )
        )
        everySuspending { catalogInteractor.fetchMaps() } returns refreshedMaps

        model.onPullToRefresh()
        advanceTimeBy(2500L) // Ensure coroutine completes

        with(model.uiState.value.model) {
            assertEquals(maps, outputMaps)
            assertFalse(isLoadingInProgress)
        }
    }
    @Test
    fun `onPullToRefresh refreshes maps empty and updates state`() = runTest {
        val refreshedMaps = listOf<CatalogMindMap>()
        val outputMaps = listOf<MapCatalogElement>()
        everySuspending { catalogInteractor.fetchMaps() } returns refreshedMaps

        model.onPullToRefresh()
        advanceTimeBy(2500L) // Ensure coroutine completes

        with(model.uiState.value.model) {
            assertEquals(maps, outputMaps)
            assertFalse(isLoadingInProgress)
        }
    }

    @Test
    fun `onMapElementClick navigates to MapDestination with correct id`() = runTest {
        val mapId = "1"
        model.onMapElementClick(mapId)

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.MapDestination)
        assertEquals(mapId, (model.uiState.value.navigationEvents.first() as NavigationTarget.MapDestination).params.id)
    }

    @Test
    fun `logout navigates to EnterPinScreenDestination`() = runTest {
        model.logout()

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.EnterPinScreenDestination)
    }

    @Test
    fun `createNewMindMap navigates to CreateMindMapDestination`() = runTest {
        model.createNewMindMap()

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.CreateMindMapDestination)
    }

    @Test
    fun `searchMindMap navigates to SearchMapDestination`() = runTest {
        model.searchMindMap()

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.SearchMapDestination)
    }

    @Test
    fun `onMigrateButtonClick navigates to MigrateMapDestination`() = runTest {
        model.onMigrateButtonClick()

        assertTrue(model.uiState.value.navigationEvents.first() is NavigationTarget.MigrateMapDestination)
    }

    // Error handling tests for `onStarted`
    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `onStarted handles errors gracefully`() = runTest {
        val errorMessage = "Error fetching maps"
        everySuspending { catalogInteractor.getMaps() } runs { throw Exception(errorMessage) }

        model.onStarted()
        advanceTimeBy(500) // Adjust based on your coroutine setup

        assertFalse(model.uiState.value.alertErrors.isEmpty())
        assertEquals(errorMessage, model.uiState.value.alertErrors.first().message)
    }

    // Similar error handling test for `onPullToRefresh`
    @Test
    @Suppress("TooGenericExceptionThrown")
    fun `onPullToRefresh handles errors gracefully`() = runTest {
        val errorMessage = "Network error"
        everySuspending { catalogInteractor.fetchMaps() } runs { throw Exception(errorMessage) }

        model.onPullToRefresh()
        advanceTimeBy(2500L)

        assertFalse(model.uiState.value.alertErrors.isEmpty())
        assertEquals(errorMessage, model.uiState.value.alertErrors.first().message)
    }
}
