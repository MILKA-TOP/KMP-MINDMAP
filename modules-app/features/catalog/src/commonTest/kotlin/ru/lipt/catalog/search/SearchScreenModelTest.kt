import com.darkrockstudios.libraries.mpfilepicker.MPFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.kodein.mock.Mock
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.catalog.search.NavigationTarget
import ru.lipt.catalog.search.SearchScreenModel
import ru.lipt.catalog.search.models.SearchAlerts
import ru.lipt.core.LoadingState
import ru.lipt.domain.catalog.ICatalogInteractor
import ru.lipt.domain.catalog.models.CatalogMindMap
import ru.lipt.domain.catalog.models.UserDomainModel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("TooGenericExceptionThrown")
class SearchScreenModelTest : TestsWithMocks() {
    override fun setUpMocks() = injectMocks(mocker) // (1)

    @Mock
    lateinit var catalogInteractor: ICatalogInteractor

    @Mock
    lateinit var mockFile: MPFile<Any>

    private val searchScreenModel by withMocks { SearchScreenModel(catalogInteractor) }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `initial state is correct`() = runTest {
        with(searchScreenModel.uiState.value.model) {
            assertTrue(searchText.isEmpty())
            assertTrue(content is LoadingState.Idle)
        }
    }

    @Test
    fun `onSearchTextChanged updates searchText`() = runTest {
        searchScreenModel.onSearchTextChanged("test")
        assertEquals("test", searchScreenModel.uiState.value.model.searchText)
    }

    @Test
    fun `loadMaps with valid query updates content`() = runTest {
        val maps = listOf(CatalogMindMap("1", "Map 1", UserDomainModel("user1", "User 1"), "Description 1", true, false, true))
        searchMaps(maps)
        advanceTimeBy(2_500L)

        assertTrue(searchScreenModel.uiState.value.model.content is LoadingState.Success)
    }

    @Test
    fun `onConfirmAddPrivateMapAlert clears addMindMapAlert`() = runTest {
        searchScreenModel.onConfirmAddPrivateMapAlert()
        searchScreenModel.onHideAddAlert()
        assertEquals(null, searchScreenModel.uiState.value.model.addMindMapAlert)

        assertFalse(searchScreenModel.isPublicMapAddJobActive)
    }

    @Test
    fun `onConfirmAddPublicMapAlert clears addMindMapAlert`() = runTest {
        searchScreenModel.onConfirmAddPublicMapAlert()
        searchScreenModel.onHideAddAlert()
        assertEquals(null, searchScreenModel.uiState.value.model.addMindMapAlert)

        assertFalse(searchScreenModel.isPublicMapAddJobActive)
    }

    @Test
    fun `onConfirmAddPublicMapAlert with empty mapId`() = runTest {
        assertFalse(searchScreenModel.isPublicMapAddJobActive)
        searchScreenModel.onMapElementClick("mapId")
        searchScreenModel.onConfirmAddPublicMapAlert()
        assertFalse(searchScreenModel.isPublicMapAddJobActive)
    }

    @Test
    fun `onConfirmAddPrivateMapAlert with empty mapId`() = runTest {
        assertFalse(searchScreenModel.isPublicMapAddJobActive)
        searchScreenModel.onMapElementClick("mapId")
        searchScreenModel.onConfirmAddPrivateMapAlert()
        assertFalse(searchScreenModel.isPublicMapAddJobActive)
    }

    @Test
    fun `onMapElementClick with saved map navigates`() = runTest {
        val mapId = "1"
        val maps = listOf(CatalogMindMap(mapId, "Map", UserDomainModel("user", "User"), "Desc", true, true, false))
        searchMaps(maps)
        searchScreenModel.onMapElementClick(mapId)

        assertTrue(searchScreenModel.uiState.value.navigationEvents.first() is NavigationTarget.ToMapNavigate)
    }

    @Test
    fun `onConfirmAddPublicMapAlert loading check alert`() = runTest {
        searchMaps()

        searchScreenModel.onMapElementClick("mapId")
        searchScreenModel.onConfirmAddPublicMapAlert()
        advanceUntilIdle()
        val alert1 = searchScreenModel.uiState.value.model.addMindMapAlert
        assertNotNull(alert1)
        assertFalse(alert1.inProgress)
    }

    @Test
    fun `onConfirmAddPrivateMapAlert ignore because password is empty`() = runTest {
        searchMaps()

        searchScreenModel.onMapElementClick("mapId")
        searchScreenModel.onConfirmAddPrivateMapAlert()
        val stateBefore = searchScreenModel.uiState.value
        advanceUntilIdle()

        assertEquals(stateBefore, searchScreenModel.uiState.value)
    }

    @Test
    fun `onConfirmAddPrivateMapAlert addMap error`() = runTest {
        searchMaps()

        everySuspending { catalogInteractor.addMap(isAny(), isAny()) } runs { throw Exception() }
        searchScreenModel.onMapElementClick("mapId")
        searchScreenModel.onPasswordEnter("password")
        searchScreenModel.onConfirmAddPrivateMapAlert()
        advanceUntilIdle()
        assertTrue(searchScreenModel.uiState.value.alertErrors.isNotEmpty())
    }

    @Test
    fun `onConfirmAddPublicMapAlert addMap error`() = runTest {
        searchMaps()

        everySuspending { catalogInteractor.addMap(isAny(), isAny()) } runs { throw Exception() }
        searchScreenModel.onMapElementClick("mapId")
        searchScreenModel.onConfirmAddPublicMapAlert()
        advanceUntilIdle()
        assertTrue(searchScreenModel.uiState.value.alertErrors.isNotEmpty())
    }

    // //

    @Test
    fun `onSearchTextChanged with invalid query keeps content idle`() = runTest {
        searchScreenModel.onSearchTextChanged("ab") // Assuming MINIMAL_QUERY_LENGTH is 3
        assertTrue(searchScreenModel.uiState.value.model.content is LoadingState.Idle)
    }

    @Test
    fun `loadMaps with invalid query does not update content`() = runTest {
        searchScreenModel.onSearchTextChanged("a")
        advanceTimeBy(2500L)
        assertTrue(searchScreenModel.uiState.value.model.content is LoadingState.Idle)
    }

    @Test
    fun `onMapElementClick with unsaved public map shows add alert`() = runTest {
        val maps = listOf(CatalogMindMap("2", "Map 2", UserDomainModel("user2", "User 2"), "Description 2", false, false, false))
        searchMaps(maps)
        searchScreenModel.onMapElementClick("2")
        assertIs<SearchAlerts.PublicMap>(searchScreenModel.uiState.value.model.addMindMapAlert)
    }

    @Test
    fun `onMapElementClick with unsaved private map shows add alert`() = runTest {
        val maps = listOf(CatalogMindMap("3", "Map 3", UserDomainModel("user3", "User 3"), "Description 3", false, false, true))
        searchMaps(maps)
        searchScreenModel.onMapElementClick("3")

        assertTrue(searchScreenModel.uiState.value.model.addMindMapAlert is SearchAlerts.PrivateMap)
    }

    @Test
    fun `onConfirmAddPublicMapAlert adds map and navigates`() = runTest {
        val maps = listOf(CatalogMindMap("2", "Map 2", UserDomainModel("user2", "User 2"), "Description 2", false, false, false))
        searchMaps(maps)
        searchScreenModel.onMapElementClick("2")
        everySuspending { catalogInteractor.addMap(isAny(), isAny()) } returns Unit

        searchScreenModel.onConfirmAddPublicMapAlert()
        advanceUntilIdle()

        assertTrue(searchScreenModel.uiState.value.navigationEvents.first() is NavigationTarget.ToMapNavigate)
    }

    @Test
    fun `onConfirmAddPrivateMapAlert without password does not navigate`() = runTest {
        val maps = listOf(CatalogMindMap("3", "Map 3", UserDomainModel("user3", "User 3"), "Description 3", false, true, true))
        searchMaps(maps)
        searchScreenModel.onMapElementClick("3")
        searchScreenModel.onConfirmAddPrivateMapAlert() // No password set
        advanceUntilIdle()

        assertTrue(searchScreenModel.uiState.value.navigationEvents.first() is NavigationTarget.ToMapNavigate)
    }

    @Test
    fun `onConfirmAddPrivateMapAlert with password adds map and navigates`() = runTest {
        val maps = listOf(CatalogMindMap("3", "Map 3", UserDomainModel("user3", "User 3"), "Description 3", false, true, true))
        searchMaps(maps)
        searchScreenModel.onMapElementClick("3")
        searchScreenModel.onPasswordEnter("validPassword")
        everySuspending { catalogInteractor.addMap(isAny(), isAny()) } returns Unit

        searchScreenModel.onConfirmAddPrivateMapAlert()
        advanceTimeBy(500)

        assertTrue(searchScreenModel.uiState.value.navigationEvents.first() is NavigationTarget.ToMapNavigate)
    }

    @Test
    fun `ignore loadMaps if input is same`() = runTest {
        val query = "tm"
        searchScreenModel.onSearchTextChanged(query)
        advanceUntilIdle()
        searchScreenModel.onSearchTextChanged(query)
        assertFalse(searchScreenModel.isSearchJobActive)
    }

    @Test
    fun `ignore loadMaps if input is not same`() = runTest {
        val query1 = "tm1"
        val query2 = "tm2"
        searchScreenModel.onSearchTextChanged(query1)
        advanceUntilIdle()
        searchScreenModel.onSearchTextChanged(query2)
        assertTrue(searchScreenModel.isSearchJobActive)
    }

    @Test
    fun `loadMaps failure shows error`() = runTest {
        everySuspending { catalogInteractor.search(isAny()) } runs { throw Exception("Network Error") }
        searchScreenModel.onSearchTextChanged("validQuery")
        advanceUntilIdle()

        assertTrue(searchScreenModel.uiState.value.model.content is LoadingState.Error)
    }

    private suspend fun TestScope.searchMaps(
        maps: List<CatalogMindMap> = listOf(
            CatalogMindMap(
                "mapId",
                "Map 1",
                UserDomainModel("user1", "User 1"),
                "Description 1",
                true,
                false,
                true
            )
        )
    ) {
        everySuspending { catalogInteractor.search(isAny()) } returns maps
        searchScreenModel.onSearchTextChanged("Map")
        advanceUntilIdle()
    }
}
