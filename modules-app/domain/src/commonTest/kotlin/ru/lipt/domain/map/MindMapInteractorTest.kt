package ru.lipt.domain.map

import kotlinx.coroutines.test.runTest
import org.kodein.mock.Fake
import org.kodein.mock.Mock
import org.kodein.mock.UsesFakes
import org.kodein.mock.tests.TestsWithMocks
import ru.lipt.core.cache.CacheEntry
import ru.lipt.core.cache.CachePolicy
import ru.lipt.domain.catalog.CatalogDataSource
import ru.lipt.domain.catalog.CatalogLocalDataSource
import ru.lipt.domain.catalog.CatalogRepository
import ru.lipt.domain.catalog.models.CatalogMindMap
import ru.lipt.domain.catalog.models.fakeCatalogMindMap
import ru.lipt.domain.map.models.AnswersEditResponseRemote
import ru.lipt.domain.map.models.MapRemoveType
import ru.lipt.domain.map.models.NodesEditResponseRemote
import ru.lipt.domain.map.models.NodesViewResponseRemote
import ru.lipt.domain.map.models.QuestionType
import ru.lipt.domain.map.models.QuestionsEditResponseRemote
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.domain.map.models.TestResultViewResponseRemote
import ru.lipt.domain.map.models.TestingCompleteRequestRemote
import ru.lipt.domain.map.models.TestsEditResponseRemote
import ru.lipt.domain.map.models.TestsViewResponseRemote
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote
import ru.lipt.domain.map.models.fakeAnswersEditResponseRemote
import ru.lipt.domain.map.models.fakeNodesEditResponseRemote
import ru.lipt.domain.map.models.fakeNodesViewResponseRemote
import ru.lipt.domain.map.models.fakeSummaryEditMapResponseRemote
import ru.lipt.domain.map.models.fakeSummaryViewMapResponseRemote
import ru.lipt.domain.map.models.fakeTestResultViewResponseRemote
import ru.lipt.domain.map.models.fakeTestsEditResponseRemote
import ru.lipt.domain.map.models.fakeTestsViewResponseRemote
import ru.lipt.domain.map.models.update.AnswerUpdateParam
import ru.lipt.domain.map.models.update.MapsUpdateRequestParams
import ru.lipt.domain.map.models.update.QuestionUpdateParam
import ru.lipt.domain.map.models.update.UpdatedListComponent
import ru.lipt.domain.map.models.update.fakeAnswerUpdateParam
import ru.lipt.domain.map.models.update.fakeMapsUpdateRequestParams
import ru.lipt.domain.map.models.update.fakeQuestionUpdateParam
import ru.lipt.domain.session.models.Session
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("TooGenericExceptionThrown", "LargeClass")
@UsesFakes(
    Session::class,
    SummaryEditMapResponseRemote::class,
    SummaryViewMapResponseRemote::class,
    NodesEditResponseRemote::class,
    NodesViewResponseRemote::class,
    CatalogMindMap::class,
    MapsUpdateRequestParams::class,
    TestResultViewResponseRemote::class,
    TestsViewResponseRemote::class,
    TestsEditResponseRemote::class,
    QuestionsEditResponseRemote::class,
    AnswersEditResponseRemote::class,
    QuestionUpdateParam::class,
    AnswerUpdateParam::class,
)
class MindMapInteractorTest : TestsWithMocks() {

    @Fake
    lateinit var updateDataSource: MindMapUpdateLocalDataSource

    @Fake
    lateinit var mindMapLocalDataSource: MindMapLocalDataSource

    @Mock
    lateinit var mindMapDataSource: MindMapDataSource

    private var mapRepository by withMocks { MindMapRepository(updateDataSource, mindMapLocalDataSource, mindMapDataSource) }

    @Fake
    lateinit var mindViewMapLocalDataSource: MindViewMapLocalDataSource

    @Mock
    lateinit var mindViewMapDataSource: MindViewMapDataSource

    private var mapViewRepository by withMocks { MindViewMapRepository(mindViewMapLocalDataSource, mindViewMapDataSource) }

    @Fake
    lateinit var catalogLocalDataSource: CatalogLocalDataSource

    @Mock
    lateinit var catalogDataSource: CatalogDataSource

    private var catalogRepository by withMocks { CatalogRepository(catalogLocalDataSource, catalogDataSource) }

    private var interactor by withMocks { MindMapInteractor(mapRepository, mapViewRepository, catalogRepository) }

    override fun setUpMocks() = injectMocks(mocker)

    @Test
    fun `getMap delegates to mapRepository with correct id and cachePolicy`() = runTest {
        val id = "map1"
        val cachePolicy = CachePolicy.ALWAYS
        val expectedMap: SummaryEditMapResponseRemote = fakeSummaryEditMapResponseRemote()

        everySuspending { mapRepository.fetch(id, cachePolicy) } returns expectedMap

        val result = interactor.getMap(id, cachePolicy)

        assertEquals(expectedMap, result)
        verifyWithSuspend(exhaustive = false) { mapRepository.fetch(id, cachePolicy) }
    }

    @Test
    fun `fetchViewMap delegates to mapViewRepository with correct parameters`() = runTest {
        val mapId = "map1"
        val userId = "user1"
        val cachePolicy = CachePolicy.ALWAYS
        val expectedViewMap: SummaryViewMapResponseRemote = fakeSummaryViewMapResponseRemote()

        everySuspending { mapViewRepository.fetch(Pair(mapId, userId), cachePolicy) } returns expectedViewMap

        val result = interactor.fetchViewMap(mapId, userId, cachePolicy)

        assertEquals(expectedViewMap, result)
        verifyWithSuspend(exhaustive = false) { mapViewRepository.fetch(Pair(mapId, userId), cachePolicy) }
    }

    @Test
    fun `getMap returns null when map is not found`() = runTest {
        val id = "nonExistentMap"
        val cachePolicy = CachePolicy.ALWAYS

        everySuspending { mapRepository.fetch(id, cachePolicy) } returns null

        val result = runCatching { interactor.getMap(id, cachePolicy) }.getOrNull()

        assertNull(result)
        verifyWithSuspend(exhaustive = false) { mapRepository.fetch(id, cachePolicy) }
    }

    @Test
    fun `fetchViewMap throws when view map is not found`() = runTest {
        val mapId = "map1"
        val userId = "user1"
        val cachePolicy = CachePolicy.ALWAYS

        everySuspending { mapViewRepository.fetch(Pair(mapId, userId), cachePolicy) } returns null

        assertFailsWith<NullPointerException> {
            interactor.fetchViewMap(mapId, userId, cachePolicy)
        }
    }

    @Test
    fun `fetchViewMap handles different cache policies correctly`() = runTest {
        val mapId = "map1"
        val userId = "user2"
        val expectedViewMap: SummaryViewMapResponseRemote = fakeSummaryViewMapResponseRemote()

        everySuspending { mapViewRepository.fetch(Pair(mapId, userId), CachePolicy.REFRESH) } returns expectedViewMap
        val result = interactor.fetchViewMap(mapId, userId, CachePolicy.REFRESH)

        assertEquals(expectedViewMap, result)
        verifyWithSuspend(exhaustive = false) { mapViewRepository.fetch(Pair(mapId, userId), CachePolicy.REFRESH) }
    }

    // addNewNodeToMap() tests

    @Test
    fun `addNewNodeToMap successfully adds node and returns updated map`() = runTest {
        val mapId = "map1"
        val parentId = "parent1"
        val title = "New Node"
        val originalMap = fakeSummaryEditMapResponseRemote()
        val updatedMap: SummaryMapResponseRemote = fakeSummaryEditMapResponseRemote()

        // Mock repository behavior
        everySuspending { mindMapDataSource.fetch(isAny()) } returns originalMap
        everySuspending { mapRepository.fetch(isEqual(mapId), CachePolicy.ALWAYS) } returns updatedMap
        interactor.getMap(mapId)

        val result = interactor.addNewNodeToMap(mapId, parentId, title)

        assertTrue(result is SummaryEditMapResponseRemote)
        assertEquals(originalMap.nodes.size + 1, result.nodes.size)
        verifyWithSuspend(exhaustive = false) { interactor.getMap(mapId) }
    }

    @Test
    fun `addNewNodeToMap returns null when map does not exist`() = runTest {
        val mapId = "nonExistentMap"
        val parentId = "parent1"
        val title = "New Node"

        assertFails { interactor.addNewNodeToMap(mapId, parentId, title) }
    }

    // Test verifies correct priorityPosition assignment for new node
    @Test
    fun `addNewNodeToMap assigns correct priorityPosition`() = runTest {
        val mapId = "map1"
        val parentId = "parent1"
        val title = "New Node"
        val originalMap: SummaryMapResponseRemote = fakeSummaryEditMapResponseRemote()
        everySuspending { mindMapDataSource.fetch(isAny()) } returns originalMap
        interactor.getMap(mapId)

        val result = interactor.addNewNodeToMap(mapId, parentId, title)

        assertTrue(result is SummaryEditMapResponseRemote)
        assertEquals(1, result.nodes.first().priorityPosition)
    }

    @Test
    fun `ignore addNewNode to when view map`() = runTest {
        val mapId = "map1"
        val parentId = "parent1"
        val title = "New Node"
        val originalMap: SummaryMapResponseRemote = fakeSummaryViewMapResponseRemote()
        everySuspending { mindMapDataSource.fetch(isAny()) } returns originalMap
        interactor.getMap(mapId)

        val result = interactor.addNewNodeToMap(mapId, parentId, title)
        assertTrue(result is SummaryViewMapResponseRemote)
        assertEquals(originalMap, result)
    }

    // getEditableNode
    @Test
    fun `getEditableNode retrieves the correct node from an editable map`() = runTest {
        val mapId = "map1"
        val nodeId = "node1"
        val expectedNode: NodesEditResponseRemote = fakeNodesEditResponseRemote().copy(id = nodeId)
        val editableMap: SummaryEditMapResponseRemote = fakeSummaryEditMapResponseRemote().copy(nodes = listOf(expectedNode))
        everySuspending { mindMapDataSource.fetch(isAny()) } returns editableMap
        interactor.getMap(mapId)

        val result = interactor.getEditableNode(mapId, nodeId)

        assertEquals(expectedNode, result)
    }

    @Test
    fun `getEditableNode retrieves the error from an view map`() = runTest {
        val mapId = "map1"
        val nodeId = "node1"
        val expectedNode: NodesViewResponseRemote = fakeNodesViewResponseRemote().copy(id = nodeId)
        val editableMap: SummaryViewMapResponseRemote = fakeSummaryViewMapResponseRemote().copy(nodes = listOf(expectedNode))
        everySuspending { mindMapDataSource.fetch(isAny()) } returns editableMap
        interactor.getMap(mapId)

        assertFails { interactor.getEditableNode(mapId, nodeId) }
    }

    // Test for getViewNode
    @Test
    fun `getViewNode retrieves the correct node from a view map`() = runTest {
        val mapId = "map1"
        val nodeId = "node1"
        val expectedNode: NodesViewResponseRemote = fakeNodesViewResponseRemote().copy(id = nodeId)
        val editableMap: SummaryViewMapResponseRemote = fakeSummaryViewMapResponseRemote().copy(nodes = listOf(expectedNode))
        everySuspending { mindMapDataSource.fetch(isAny()) } returns editableMap
        interactor.getMap(mapId)

        val result = interactor.getViewNode(mapId, nodeId)

        assertEquals(expectedNode, result)
    } // Test for getViewNode

    @Test
    fun `getViewNode retrieves the error from an view map`() = runTest {
        val mapId = "map1"
        val nodeId = "node1"
        val expectedNode: NodesEditResponseRemote = fakeNodesEditResponseRemote().copy(id = nodeId)
        val editableMap: SummaryEditMapResponseRemote = fakeSummaryEditMapResponseRemote().copy(nodes = listOf(expectedNode))
        everySuspending { mindMapDataSource.fetch(isAny()) } returns editableMap
        interactor.getMap(mapId)

        assertFails { interactor.getViewNode(mapId, nodeId) }
    }

    @Test
    fun `deleteMap successfully deletes and removes map from catalog`() = runTest {
        val mapId = "map1"
        val originalMap: SummaryMapResponseRemote = fakeSummaryViewMapResponseRemote().copy(id = mapId)
        val catalogMap: CatalogMindMap = fakeCatalogMindMap().copy(id = mapId)
        everySuspending { mindMapDataSource.fetch(isAny()) } returns originalMap
        everySuspending { catalogDataSource.fetch(Unit) } returns listOf(catalogMap)
        interactor.getMap(mapId)
        catalogRepository.fetch(Unit, CachePolicy.ALWAYS)

        everySuspending { mindMapDataSource.deleteMap(mapId) } returns Unit

        interactor.deleteMap(mapId)
        val catalogList = catalogRepository.fetch(Unit, CachePolicy.ALWAYS).orEmpty()

        assertNull(catalogList.firstOrNull { it.id == mapId })

        verifyWithSuspend(exhaustive = false) { mapRepository.deleteMap(mapId) }
        verifyWithSuspend(exhaustive = false) { catalogRepository.removeMap(mapId) }
    }

    @Test
    fun `deleteMap handles failure from mapRepository gracefully`() = runTest {
        val mapId = "testMapId"

        everySuspending { mindMapDataSource.deleteMap(mapId) } runs { throw Exception("Deletion failed") }

        assertFailsWith<Exception> {
            interactor.deleteMap(mapId)
        }
    }

    @Test
    fun `erase successfully deletes and removes map from catalog`() = runTest {
        val mapId = "map1"
        val originalMap: SummaryMapResponseRemote = fakeSummaryViewMapResponseRemote().copy(id = mapId)
        val catalogMap: CatalogMindMap = fakeCatalogMindMap().copy(id = mapId)
        everySuspending { mindMapDataSource.fetch(isAny()) } returns originalMap
        everySuspending { catalogDataSource.fetch(Unit) } returns listOf(catalogMap)
        interactor.getMap(mapId)
        catalogRepository.fetch(Unit, CachePolicy.ALWAYS)

        everySuspending { mindMapDataSource.eraseMap(mapId, MapRemoveType.DELETE) } returns Unit

        interactor.eraseMap(mapId, MapRemoveType.DELETE)
        val catalogList = catalogRepository.fetch(Unit, CachePolicy.ALWAYS).orEmpty()

        assertNull(catalogList.firstOrNull { it.id == mapId })

        verifyWithSuspend(exhaustive = false) { mapRepository.eraseMap(mapId, MapRemoveType.DELETE) }
        verifyWithSuspend(exhaustive = false) { catalogRepository.removeMap(mapId) }
    }

    @Test
    fun `eraseMap handles failure from mapRepository gracefully`() = runTest {
        val mapId = "testMapId"

        everySuspending { mindMapDataSource.eraseMap(isEqual(mapId), isAny()) } runs { throw Exception("Deletion failed") }

        assertFailsWith<Exception> {
            interactor.eraseMap(mapId, MapRemoveType.DELETE)
        }
    }

    @Test
    fun `updateMindMap updates and fetches the map successfully and empty updateDataSource`() = runTest {
        val mapId = "testMapId"
        val expectedUpdatedMap: SummaryMapResponseRemote = fakeSummaryEditMapResponseRemote()
        val updateParams = fakeMapsUpdateRequestParams()

        everySuspending { mindMapDataSource.updateMindMap(mapId, updateParams) } returns Unit
        everySuspending { mapRepository.fetch(mapId, CachePolicy.REFRESH) } returns expectedUpdatedMap

        val result = interactor.updateMindMap(mapId)

        assertEquals(expectedUpdatedMap, result)
        verifyWithSuspend(exhaustive = false) { mapRepository.updateMindMap(mapId) }
    }

    @Test
    fun `updateMindMap updates and fetches the map successfully and non-empty updateDataSource`() = runTest {
        val mapId = "testMapId"
        val expectedUpdatedMap: SummaryMapResponseRemote = fakeSummaryEditMapResponseRemote()
        val updateParams = fakeMapsUpdateRequestParams()

        everySuspending { mindMapDataSource.updateMindMap(mapId, updateParams) } returns Unit
        everySuspending { mapRepository.fetch(mapId, CachePolicy.REFRESH) } returns expectedUpdatedMap
        updateDataSource.set(mapId, CacheEntry(mapId, MapsUpdateRequestParams("", "")))

        val result = interactor.updateMindMap(mapId)

        assertEquals(expectedUpdatedMap, result)
        verifyWithSuspend(exhaustive = false) { mapRepository.updateMindMap(mapId) }
    }

    @Test
    fun `updateMindMap handles null fetch result appropriately`() = runTest {
        val mapId = "testMapId"

        everySuspending { mindMapDataSource.updateMindMap(isEqual(mapId), isAny()) } returns Unit
        everySuspending { mapRepository.fetch(mapId, CachePolicy.REFRESH) } returns null

        assertFailsWith<NullPointerException> {
            interactor.updateMindMap(mapId)
        }
    }

    @Test
    fun `updateMindMap handles exceptions during update operation gracefully`() = runTest {
        val mapId = "testMapId"

        everySuspending { mindMapDataSource.updateMindMap(isEqual(mapId), isAny()) } runs { throw Exception("Update failed") }

        assertFailsWith<Exception> {
            interactor.updateMindMap(mapId)
        }
    }

    // updateNodePosition region

    @Test
    fun `updateNodePosition successfully updates node's priority position and fetches updated map`() = runTest {
        val mapId = "mapId"
        val nodeId1 = "nodeId1"
        val nodeId2 = "nodeId2"
        val index = 1
        val expectedNode1: NodesEditResponseRemote = fakeNodesEditResponseRemote().copy(id = nodeId1, priorityPosition = 1)
        val expectedNode2: NodesEditResponseRemote = fakeNodesEditResponseRemote().copy(id = nodeId2, priorityPosition = 2)
        val expectedUpdatedMap: SummaryMapResponseRemote =
            fakeSummaryEditMapResponseRemote().copy(nodes = listOf(expectedNode1, expectedNode2))

        everySuspending { mindMapDataSource.fetch(mapId) } returns expectedUpdatedMap
        interactor.getMap(mapId, CachePolicy.ALWAYS)

        val result = interactor.updateNodePosition(mapId, nodeId2, index)

        assertTrue(result is SummaryEditMapResponseRemote)
        assertEquals(result.nodes.first { it.id == nodeId1 }.priorityPosition, 2)
        assertEquals(result.nodes.first { it.id == nodeId2 }.priorityPosition, 1)
    }

    @Test
    fun `updateNodePosition handles non-existent node gracefully`() = runTest {
        val mapId = "mapId"
        val nonExistentNodeId = "nonExistentNodeId"
        val index = 2
        val editableMap: SummaryEditMapResponseRemote = fakeSummaryEditMapResponseRemote()
        setupMap(mapId)

        everySuspending { mindMapDataSource.fetch(mapId) } returns editableMap

        assertFailsWith<NoSuchElementException> {
            interactor.updateNodePosition(mapId, nonExistentNodeId, index)
        }
    }

    @Test
    fun `updateNodePosition correctly affects nodes within the same parent section`() = runTest {
        val mapId = "mapId"
        val nodeId = "nodeId"
        val index = 0
        val expectedNode: NodesEditResponseRemote = fakeNodesEditResponseRemote().copy(id = nodeId, priorityPosition = 1)
        val expectedUpdatedMap: SummaryMapResponseRemote = fakeSummaryEditMapResponseRemote().copy(nodes = listOf(expectedNode))
        everySuspending { mindMapDataSource.fetch(mapId) } returns expectedUpdatedMap
        interactor.getMap(mapId, CachePolicy.ALWAYS)
        everySuspending { mindMapDataSource.fetch(mapId) } returns expectedUpdatedMap

        interactor.updateNodePosition(mapId, nodeId, index)
    }

    @Test
    fun `updateNodePosition does not operate on non-SummaryEditMapResponseRemote instances`() = runTest {
        val mapId = "mapId"
        val nodeId = "nodeId"
        val index = 2
        val nonEditableMap: SummaryMapResponseRemote = fakeSummaryViewMapResponseRemote()

        everySuspending { mindMapDataSource.fetch(mapId) } returns nonEditableMap
        interactor.getMap(mapId)

        val result = interactor.updateNodePosition(mapId, nodeId, index)

        assertTrue(result is SummaryViewMapResponseRemote)
        assertEquals(nonEditableMap, result)
    }

    @Test
    fun `updateNodePosition successfully updates node's priority position and fetches updated map 1`() = runTest {
        val mapId = "mapId"
        val nodeId1 = "nodeId1"
        val nodeId2 = "nodeId2"
        val index = 2
        val expectedNode1: NodesEditResponseRemote = fakeNodesEditResponseRemote().copy(id = nodeId1, priorityPosition = 1)
        val expectedNode2: NodesEditResponseRemote = fakeNodesEditResponseRemote().copy(id = nodeId2, priorityPosition = 2)
        val expectedUpdatedMap: SummaryMapResponseRemote =
            fakeSummaryEditMapResponseRemote().copy(nodes = listOf(expectedNode1, expectedNode2))

        everySuspending { mindMapDataSource.fetch(mapId) } returns expectedUpdatedMap
        interactor.getMap(mapId, CachePolicy.ALWAYS)

        val result = interactor.updateNodePosition(mapId, nodeId1, index)

        assertTrue(result is SummaryEditMapResponseRemote)
        assertEquals(result.nodes.first { it.id == nodeId1 }.priorityPosition, 2)
        assertEquals(result.nodes.first { it.id == nodeId2 }.priorityPosition, 1)
    }

    // saveTitleAndData

    @Test
    fun `saveTitleAndData updates title and description successfully for editable maps`() = runTest {
        val mapId = "mapId"
        val newTitle = "Updated Title"
        val newDescription = "Updated Description"
        val editableMap = fakeSummaryEditMapResponseRemote().copy(mapId, title = "Old Title", description = "Old Description")

        everySuspending { mindMapDataSource.fetch(mapId) } returns editableMap
        interactor.getMap(mapId, CachePolicy.REFRESH)

        interactor.saveTitleAndData(mapId, newTitle, newDescription)
        val result = interactor.getMap(mapId, CachePolicy.ALWAYS)

        assertTrue(result is SummaryEditMapResponseRemote)
        assertEquals(newTitle, result.title)
        assertEquals(newDescription, result.description)
    }

    @Test
    fun `saveTitleAndData updates title and description idle for view maps`() = runTest {
        val mapId = "mapId"
        val newTitle = "Updated Title"
        val oldTitle = "Old Title"
        val newDescription = "Updated Description"
        val oldDescription = "Old Description"
        val viewMap = fakeSummaryViewMapResponseRemote().copy(mapId, title = oldTitle, description = oldDescription)

        everySuspending { mindMapDataSource.fetch(mapId) } returns viewMap
        interactor.getMap(mapId, CachePolicy.REFRESH)

        interactor.saveTitleAndData(mapId, newTitle, newDescription)
        val result = interactor.getMap(mapId, CachePolicy.ALWAYS)

        assertTrue(result is SummaryViewMapResponseRemote)
        assertEquals(oldTitle, result.title)
        assertEquals(oldDescription, result.description)
    }

    // toggle node region

    @Test
    fun `toggleNode successfully toggles a node and returns the new state`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val toggledState = true
        setupMap(mapId)

        everySuspending { mindMapDataSource.toggleNode(nodeId) } returns toggledState

        val result = interactor.toggleNode(mapId, nodeId)

        assertEquals(result, toggledState)
        verifyWithSuspend(exhaustive = false) { mapRepository.toggleNode(mapId, nodeId) }
    }

    @Test
    fun `toggleNode successfully toggles a node with nodes list and returns the new state`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val toggledState = true
        val originalMap: SummaryMapResponseRemote = fakeSummaryViewMapResponseRemote().copy(
            id = mapId,
            nodes = listOf(
                fakeNodesViewResponseRemote().copy(id = "${nodeId}1"),
                fakeNodesViewResponseRemote().copy(id = "${nodeId}2"),
            )
        )
        everySuspending { mindMapDataSource.fetch(isAny()) } returns originalMap
        interactor.getMap(mapId)

        everySuspending { mindMapDataSource.toggleNode(nodeId) } returns toggledState

        val result = interactor.toggleNode(mapId, nodeId)

        assertEquals(result, toggledState)
        verifyWithSuspend(exhaustive = false) { mapRepository.toggleNode(mapId, nodeId) }
    }

    @Test
    fun `toggleNode handles repository exceptions gracefully`() = runTest {
        val mapId = "testMapId"
        setupMap(mapId)
        val nodeId = "testNodeId"
        val exceptionMessage = "Error toggling node"

        everySuspending { mapRepository.toggleNode(mapId, nodeId) } runs { throw Exception(exceptionMessage) }

        assertFailsWith<Exception>(exceptionMessage) {
            interactor.toggleNode(mapId, nodeId)
        }.apply {
            assertEquals(exceptionMessage, message)
        }
    }

    @Test
    fun `toggleNode on a non-existent map`() = runTest {
        val nonExistentMapId = "nonExistentMapId"
        val nodeId = "testNodeId"
        everySuspending { mapRepository.toggleNode(nonExistentMapId, nodeId) } returns false

        assertFails { interactor.toggleNode(nonExistentMapId, nodeId) }
    }

    @Test
    fun `toggleNode of view map`() = runTest {
        val mapId = "mapId"
        val nodeId = "nodeId"
        val node = fakeNodesViewResponseRemote().copy(id = nodeId)
        val originalMap = fakeSummaryViewMapResponseRemote().copy(id = mapId, nodes = listOf(node))
        everySuspending { mapRepository.toggleNode(mapId, nodeId) } returns false
        everySuspending { mindMapDataSource.fetch(isAny()) } returns originalMap
        interactor.getMap(mapId)
        val result = interactor.toggleNode(mapId, nodeId)

        assertEquals(node.isSelected, result)
    }

    // saveNodeData

    @Test
    fun `saveNodeData updates node data successfully for editable maps`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val newTitle = "Updated Title"
        val newDescription = "Updated Description"
        val editableMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId, nodes = listOf(
                fakeNodesEditResponseRemote().copy(id = nodeId, label = "Old Title", description = "Old Description")
            )
        )
        everySuspending { mindMapDataSource.fetch(isAny()) } returns editableMapBefore
        interactor.getMap(mapId)

        interactor.saveNodeData(mapId, nodeId, newTitle, newDescription)
        val result = interactor.getMap(mapId)
        assertTrue(result is SummaryEditMapResponseRemote)
        val node = result.nodes.first { it.id == nodeId }
        assertEquals(newTitle, node.label)
        assertEquals(newDescription, node.description)
    }

    @Test
    fun `saveNodeData does not update non-editable maps`() = runTest {
        val mapId = "testMapId"
        val nonEditableMap: SummaryMapResponseRemote = fakeSummaryViewMapResponseRemote().copy(
            id = mapId,
            nodes = listOf(
                fakeNodesViewResponseRemote().copy(
                    id = "nodeId", label = "Old Title", description = "Old description"
                )
            )
        )
        everySuspending { mindMapDataSource.fetch(isAny()) } returns nonEditableMap
        interactor.getMap(mapId)

        interactor.saveNodeData(mapId, "nodeId", "New Title", "New Description")
        val result = interactor.getMap(mapId)
        assertTrue(result is SummaryViewMapResponseRemote)
        val node = result.nodes.first { it.id == "nodeId" }
        assertEquals(node.label, "Old Title")
        assertEquals(node.description, "Old description")
    }

    @Test
    fun `saveNodeData handles non-existent nodes gracefully`() = runTest {
        val mapId = "testMapId"
        val nodeId = "nonExistentNodeId"
        val editableMap: SummaryEditMapResponseRemote = fakeSummaryEditMapResponseRemote().copy(id = mapId)
        everySuspending { mindMapDataSource.fetch(isAny()) } returns editableMap
        interactor.getMap(mapId)

        assertFails {
            interactor.saveNodeData(mapId, nodeId, "New Title", "New Description")
        }
    }

    @Test
    fun `saveNodeData updates node data of map with more than one node successfully for editable maps`() = runTest {
        val mapId = "testMapId"
        val nodeId1 = "testNodeId1"
        val nodeId2 = "testNodeId2"
        val newTitle = "Updated Title"
        val newDescription = "Updated Description"
        val editableMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId, nodes = listOf(
                fakeNodesEditResponseRemote().copy(id = nodeId1, label = "Old Title", description = "Old Description"),
                fakeNodesEditResponseRemote().copy(id = nodeId2, label = "Old Title", description = "Old Description")

            )
        )
        everySuspending { mindMapDataSource.fetch(isAny()) } returns editableMapBefore
        interactor.getMap(mapId)

        interactor.saveNodeData(mapId, nodeId2, newTitle, newDescription)
        val result = interactor.getMap(mapId)
        assertTrue(result is SummaryEditMapResponseRemote)
        val node = result.nodes.first { it.id == nodeId2 }
        assertEquals(newTitle, node.label)
        assertEquals(newDescription, node.description)
    }

    // removeNode

    @Test
    fun `removeNode successfully removes a node and reassigns child nodes`() = runTest {
        val mapId = "testMapId"
        val nodeIdToRemove = "nodeIdToRemove"
        val parentNodeId = "parentNodeId"
        val childNodeId = "childNodeId"
        val editableMap = fakeSummaryEditMapResponseRemote().copy(
            id = mapId, nodes = listOf(
                fakeNodesEditResponseRemote().copy(nodeIdToRemove, parentNodeId = parentNodeId, label = "Node To Remove"),
                fakeNodesEditResponseRemote().copy(childNodeId, parentNodeId = nodeIdToRemove, label = "Child Node")
            )
        )
        everySuspending { mindMapDataSource.fetch(isAny()) } returns editableMap
        interactor.getMap(mapId)

        interactor.removeNode(mapId, nodeIdToRemove)
        val result = interactor.getMap(mapId)
        assertTrue(result is SummaryEditMapResponseRemote)
        assertFalse(result.nodes.any { it.id == nodeIdToRemove })
    }

    @Test
    fun `removeNode does not update non-editable maps`() = runTest {
        val mapId = "testMapId"
        val nodeIdToRemove = "nodeIdToRemove"
        val nonEditableMap: SummaryMapResponseRemote = fakeSummaryViewMapResponseRemote().copy(
            id = mapId, nodes = listOf(
                fakeNodesViewResponseRemote().copy(nodeIdToRemove, label = "Node To Remove"),
            )
        )

        everySuspending { mindMapDataSource.fetch(isAny()) } returns nonEditableMap
        interactor.getMap(mapId)

        interactor.removeNode(mapId, "nodeId")
        val result = interactor.getMap(mapId)
        assertTrue(result is SummaryViewMapResponseRemote)
        assertTrue(result.nodes.any { it.id == nodeIdToRemove })
    }

    @Test
    fun `removeNode handles non-existent nodes gracefully`() = runTest {
        val mapId = "testMapId"
        val nodeIdToRemove = "nodeIdToRemove"
        val nodeIdSaved = "nodeIdSaved"
        val nonEditableMap: SummaryMapResponseRemote = fakeSummaryViewMapResponseRemote().copy(
            id = mapId, nodes = listOf(
                fakeNodesViewResponseRemote().copy(nodeIdSaved, label = "Node To Remove"),
            )
        )

        everySuspending { mindMapDataSource.fetch(isAny()) } returns nonEditableMap
        interactor.getMap(mapId)

        interactor.removeNode(mapId, nodeIdToRemove)
        val result = interactor.getMap(mapId)
        assertTrue(result is SummaryViewMapResponseRemote)
        assertFalse(result.nodes.any { it.id == nodeIdToRemove })
    }

    @Test
    fun `updateRequestCache is correctly updated following node removal`() = runTest {
        val mapId = "testMapId"
        val nodeIdToRemove = "nodeIdToRemove"
        val editableMap = fakeSummaryEditMapResponseRemote().copy(
            mapId,
            nodes = listOf(fakeNodesEditResponseRemote().copy(id = nodeIdToRemove, parentNodeId = "parentNodeId", label = "Node To Remove"))
        )
        everySuspending { mindMapDataSource.fetch(isAny()) } returns editableMap
        interactor.getMap(mapId)

        interactor.removeNode(mapId, nodeIdToRemove)

        val updateRequestCache = updateDataSource.get(mapId)?.value
        assertNotNull(updateRequestCache)
        assertTrue(updateRequestCache.nodes.removed.any { it == nodeIdToRemove })
    }

    @Test
    fun `updateRequestCache is correctly updated following node inserted empty`() = runTest {
        val mapId = "testMapId"
        val parentNodeId = "parentNodeId"
        val editableMap = fakeSummaryEditMapResponseRemote().copy(
            mapId,
        )
        everySuspending { mindMapDataSource.fetch(isAny()) } returns editableMap
        interactor.getMap(mapId)
        val updatedMap1 = interactor.addNewNodeToMap(mapId, parentNodeId, "New node title")
        assertTrue(updatedMap1 is SummaryEditMapResponseRemote)
        val addedNode = updatedMap1.nodes.first()
        val nodeIdToRemove = addedNode.id
        val updateRequestCache1 = updateDataSource.get(mapId)?.value
        assertNotNull(updateRequestCache1)
        assertTrue(updateRequestCache1.nodes.insert.any { it.nodeId == addedNode.id })

        interactor.removeNode(mapId, nodeIdToRemove)

        val updateRequestCache2 = updateDataSource.get(mapId)?.value
        assertNotNull(updateRequestCache2)
        assertFalse(updateRequestCache2.nodes.insert.any { it.nodeId == addedNode.toString() })
    }

    @Test
    fun `updateRequestCache is correctly updated following node updated empty`() = runTest {
        val mapId = "testMapId"
        val nodeId = "nodeId"
        val editableMap = fakeSummaryEditMapResponseRemote().copy(
            mapId, nodes = listOf(fakeNodesEditResponseRemote().copy(id = nodeId))
        )
        everySuspending { mindMapDataSource.fetch(isAny()) } returns editableMap
        interactor.getMap(mapId)
        // update node
        interactor.saveNodeData(mapId, nodeId, "Updated title", "Updated description")

        val updateRequestCache1 = updateDataSource.get(mapId)?.value
        assertNotNull(updateRequestCache1)
        assertTrue(updateRequestCache1.nodes.updated.any { it.nodeId == nodeId })

        interactor.removeNode(mapId, nodeId)

        val updateRequestCache2 = updateDataSource.get(mapId)?.value
        assertNotNull(updateRequestCache2)
        assertFalse(updateRequestCache2.nodes.insert.any { it.nodeId == nodeId })
    }

    // sendTestAnswersForNode

    @Test
    fun `sendTestAnswersForNode successfully submits answers and updates node test result`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val testId = "testTestId"
        val testAnswers = TestingCompleteRequestRemote(listOf())
        val testResult: TestResultViewResponseRemote = fakeTestResultViewResponseRemote()
        val viewMapBefore = fakeSummaryViewMapResponseRemote().copy(
            id = mapId, nodes = listOf(
                fakeNodesViewResponseRemote().copy(id = nodeId, test = fakeTestsViewResponseRemote())
            )
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns viewMapBefore
        interactor.getMap(mapId)

        everySuspending { mindMapDataSource.sendTestAnswersForNode(isAny(), isAny()) } returns testResult
        val result = interactor.sendTestAnswersForNode(mapId, nodeId, testId, testAnswers)

        assertEquals(testResult, result)
    }

    @Test
    fun `sendTestAnswersForNode editable map throw exception`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val testId = "testTestId"
        val testAnswers = TestingCompleteRequestRemote(listOf())
        val viewMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId, nodes = listOf(
                fakeNodesEditResponseRemote().copy(id = nodeId)
            )
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns viewMapBefore
        interactor.getMap(mapId)

        everySuspending { mindMapDataSource.sendTestAnswersForNode(isAny(), isAny()) } runs { throw IllegalArgumentException() }
        assertFails {
            interactor.sendTestAnswersForNode(mapId, nodeId, testId, testAnswers)
        }
    }

    @Test
    fun `sendTestAnswersForNode editable map`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val testId = "testTestId"
        val testAnswers = TestingCompleteRequestRemote(listOf())
        val testResult: TestResultViewResponseRemote = fakeTestResultViewResponseRemote()
        val viewMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId, nodes = listOf(
                fakeNodesEditResponseRemote().copy(id = nodeId)
            )
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns viewMapBefore
        interactor.getMap(mapId)

        everySuspending { mindMapDataSource.sendTestAnswersForNode(isAny(), isAny()) } returns testResult
        interactor.sendTestAnswersForNode(mapId, nodeId, testId, testAnswers)

        verifyWithSuspend(exhaustive = false) { mindMapLocalDataSource.set(mapId, CacheEntry(key = mapId, value = viewMapBefore)) }
    }

    @Test
    fun `sendTestAnswersForNode successfully submits answers and updates node test result with more than one node`() = runTest {
        val mapId = "testMapId"
        val nodeId1 = "testNodeId1"
        val nodeId2 = "testNodeId2"
        val testId = "testTestId"
        val testAnswers = TestingCompleteRequestRemote(listOf())
        val testResult: TestResultViewResponseRemote = fakeTestResultViewResponseRemote()
        val node1 = fakeNodesViewResponseRemote().copy(id = nodeId1, test = fakeTestsViewResponseRemote())
        val node2 = fakeNodesViewResponseRemote().copy(id = nodeId2)
        val viewMapBefore = fakeSummaryViewMapResponseRemote().copy(
            id = mapId, nodes = listOf(node1, node2)
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns viewMapBefore
        interactor.getMap(mapId)

        everySuspending { mindMapDataSource.sendTestAnswersForNode(isAny(), isAny()) } returns testResult
        interactor.sendTestAnswersForNode(mapId, nodeId1, testId, testAnswers)

        val updatedMap = interactor.getMap(mapId)
        assertTrue(updatedMap is SummaryViewMapResponseRemote)
        assertEquals(node2, updatedMap.nodes.first { it.id == nodeId2 })
        assertNotEquals(node1, updatedMap.nodes.first { it.id == nodeId1 })
    }

    // generateTest

    @Test
    fun `generateTest successfully generates test and updates node`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val generatedTest: TestsEditResponseRemote = fakeTestsEditResponseRemote()
        val editableMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId, nodes = listOf(fakeNodesEditResponseRemote().copy(id = nodeId, label = "Node Label"))
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns editableMapBefore
        interactor.getMap(mapId)

        everySuspending { mapRepository.generateTest(nodeId) } returns generatedTest

        val result = interactor.generateTest(mapId, nodeId)

        assertEquals(generatedTest, result)
    }

    @Test
    fun `generateTest does not update non-editable maps`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val nonEditableMap: SummaryMapResponseRemote = fakeSummaryViewMapResponseRemote().copy(id = mapId)
        val generatedTest: TestsEditResponseRemote = fakeTestsEditResponseRemote()

        everySuspending { mindMapDataSource.fetch(mapId) } returns nonEditableMap
        interactor.getMap(mapId)

        everySuspending { mapRepository.generateTest(nodeId) } returns generatedTest

        interactor.generateTest(mapId, nodeId)

        verifyWithSuspend(exhaustive = false) { mindMapLocalDataSource.set(mapId, CacheEntry(key = mapId, value = nonEditableMap)) }
    }

    @Test
    fun `generateTest successfully generates test and updates node with more than one node`() = runTest {
        val mapId = "testMapId"
        val nodeId1 = "testNodeId1"
        val nodeId2 = "testNodeId2"
        val generatedTest: TestsEditResponseRemote = fakeTestsEditResponseRemote()
        val node1 = fakeNodesEditResponseRemote().copy(id = nodeId1, label = "Node Label")
        val node2 = fakeNodesEditResponseRemote().copy(id = nodeId2, label = "Node Label")
        val editableMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId, nodes = listOf(node1, node2)
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns editableMapBefore
        interactor.getMap(mapId)

        everySuspending { mapRepository.generateTest(nodeId1) } returns generatedTest

        val result = interactor.generateTest(mapId, nodeId1)
        val updatedMap = interactor.getMap(mapId)

        assertTrue(updatedMap is SummaryEditMapResponseRemote)

        assertEquals(generatedTest, result)
        assertEquals(updatedMap.nodes.first { it.id == node2.id }, node2)
        assertNotEquals(updatedMap.nodes.first { it.id == node1.id }, node1)
    }

    // updateQuestions

    @Test
    fun `updateQuestions successfully updates questions and answers and empty cache`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val testId = "testTestId"
        val questions = listOf(
            QuestionsEditResponseRemote(
                id = "questionId",
                testId = testId,
                questionType = QuestionType.SINGLE_CHOICE,
                questionText = "Updated Question",
                answers = listOf()
            )
        )
        val editableMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId,
            nodes = listOf(
                fakeNodesEditResponseRemote().copy(
                    id = nodeId,
                    test = fakeTestsEditResponseRemote().copy(id = testId)
                )
            )
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns editableMapBefore
        interactor.getMap(mapId)

        interactor.updateQuestions(mapId, nodeId, testId, questions)

        verifyWithSuspend(exhaustive = false) { mindMapLocalDataSource.set(mapId, CacheEntry(key = mapId, value = isAny())) }
    }

    @Test
    fun `updateQuestions with empty null test an empty answers and empty cache`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val testId = "testTestId"
        val questions = listOf(
            QuestionsEditResponseRemote(
                id = "questionId",
                testId = testId,
                questionType = QuestionType.SINGLE_CHOICE,
                questionText = "Updated Question",
                answers = listOf()
            )
        )
        val editableMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId,
            nodes = listOf(
                fakeNodesEditResponseRemote().copy(id = nodeId)
            )
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns editableMapBefore
        interactor.getMap(mapId)

        interactor.updateQuestions(mapId, nodeId, testId, questions)

        val updateRequestCache = updateDataSource.get(mapId)?.value
        assertNotNull(updateRequestCache)
        assertTrue(updateRequestCache.questions.insert.isNotEmpty())
        assertTrue(updateRequestCache.questions.updated.isEmpty())
    }

    @Test
    fun `updateQuestions with empty null test an list of answers and empty cache`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val testId = "testTestId"
        val questions = listOf(
            QuestionsEditResponseRemote(
                id = "questionId",
                testId = testId,
                questionType = QuestionType.SINGLE_CHOICE,
                questionText = "Updated Question",
                answers = listOf(
                    fakeAnswersEditResponseRemote().copy(id = "answerId1"),
                    fakeAnswersEditResponseRemote().copy(id = "answerId2"),
                )
            )
        )
        val editableMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId,
            nodes = listOf(
                fakeNodesEditResponseRemote().copy(id = nodeId)
            )
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns editableMapBefore
        interactor.getMap(mapId)

        interactor.updateQuestions(mapId, nodeId, testId, questions)

        val updateRequestCache = updateDataSource.get(mapId)?.value
        assertNotNull(updateRequestCache)
        assertTrue(updateRequestCache.answers.insert.isNotEmpty())
        assertTrue(updateRequestCache.answers.updated.isEmpty())
    }

    @Test
    fun `updateQuestions with empty null test an list of answers and base cache - insert`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val testId = "testTestId"
        val cachedQuestionId = "cachedQuestionId"
        val cachedAnswerId = "cachedAnswerId"
        val questions = listOf(
            QuestionsEditResponseRemote(
                id = "questionId",
                testId = testId,
                questionType = QuestionType.SINGLE_CHOICE,
                questionText = "Updated Question",
                answers = listOf(
                    fakeAnswersEditResponseRemote().copy(id = "answerId1"),
                    fakeAnswersEditResponseRemote().copy(id = "answerId2"),
                )
            )
        )
        val editableMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId,
            nodes = listOf(
                fakeNodesEditResponseRemote().copy(id = nodeId)
            )
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns editableMapBefore
        interactor.getMap(mapId)
        updateDataSource.set(
            mapId, CacheEntry(
                mapId, fakeMapsUpdateRequestParams().copy(
                    questions = UpdatedListComponent(
                        insert = listOf(fakeQuestionUpdateParam().copy(questionId = cachedQuestionId))
                    ),
                    answers = UpdatedListComponent(
                        insert = listOf(fakeAnswerUpdateParam().copy(answerId = cachedAnswerId))
                    )
                )
            )
        )

        interactor.updateQuestions(mapId, nodeId, testId, questions)

        val updateRequestCache = updateDataSource.get(mapId)?.value
        assertNotNull(updateRequestCache)
        assertNotNull(updateRequestCache.answers.insert.firstOrNull { it.answerId == cachedAnswerId })
        assertNotNull(updateRequestCache.questions.insert.firstOrNull { it.questionId == cachedQuestionId })
        assertNotNull(updateRequestCache.answers.insert.firstOrNull { it.answerId == "answerId1" })
        assertNotNull(updateRequestCache.questions.insert.firstOrNull { it.questionId == "questionId" })
    }

    @Test
    fun `updateQuestions with empty null test an list of answers and base cache - updated`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val testId = "testTestId"
        val cachedQuestionId = "cachedQuestionId"
        val cachedAnswerId = "cachedAnswerId"
        val questions = listOf(
            QuestionsEditResponseRemote(
                id = "questionId",
                testId = testId,
                questionType = QuestionType.SINGLE_CHOICE,
                questionText = "Updated Question",
                answers = listOf(
                    fakeAnswersEditResponseRemote().copy(id = "answerId1"),
                    fakeAnswersEditResponseRemote().copy(id = "answerId2"),
                )
            )
        )
        val editableMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId,
            nodes = listOf(
                fakeNodesEditResponseRemote().copy(id = nodeId)
            )
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns editableMapBefore
        interactor.getMap(mapId)
        updateDataSource.set(
            mapId, CacheEntry(
                mapId, fakeMapsUpdateRequestParams().copy(
                    questions = UpdatedListComponent(
                        updated = listOf(fakeQuestionUpdateParam().copy(questionId = cachedQuestionId))
                    ),
                    answers = UpdatedListComponent(
                        updated = listOf(fakeAnswerUpdateParam().copy(answerId = cachedAnswerId))
                    )
                )
            )
        )

        interactor.updateQuestions(mapId, nodeId, testId, questions)

        val updateRequestCache = updateDataSource.get(mapId)?.value
        assertNotNull(updateRequestCache)
        assertNotNull(updateRequestCache.answers.updated.firstOrNull { it.answerId == cachedAnswerId })
        assertNotNull(updateRequestCache.questions.updated.firstOrNull { it.questionId == cachedQuestionId })
        assertNotNull(updateRequestCache.answers.insert.firstOrNull { it.answerId == "answerId1" })
        assertNotNull(updateRequestCache.questions.insert.firstOrNull { it.questionId == "questionId" })
    }

    @Test
    fun `updateQuestions with test an list of answers and base cache - updated`() = runTest {
        val mapId = "testMapId"
        val nodeId = "testNodeId"
        val testId = "testTestId"
        val cachedQuestionId = "cachedQuestionId"
        val cachedAnswerId = "cachedAnswerId"
        val questions = listOf(
            QuestionsEditResponseRemote(
                id = cachedQuestionId,
                testId = testId,
                questionType = QuestionType.SINGLE_CHOICE,
                questionText = "Updated Question",
                answers = listOf(
                    fakeAnswersEditResponseRemote().copy(id = "answerId1"),
                    fakeAnswersEditResponseRemote().copy(id = "answerId2"),
                )
            )
        )
        val editableMapBefore = fakeSummaryEditMapResponseRemote().copy(
            id = mapId,
            nodes = listOf(
                fakeNodesEditResponseRemote().copy(id = nodeId)
            )
        )

        everySuspending { mindMapDataSource.fetch(mapId) } returns editableMapBefore
        interactor.getMap(mapId)
        updateDataSource.set(
            mapId, CacheEntry(
                mapId, fakeMapsUpdateRequestParams().copy(
                    questions = UpdatedListComponent(
                        updated = listOf(fakeQuestionUpdateParam().copy(questionId = cachedQuestionId))
                    ),
                    answers = UpdatedListComponent(
                        updated = listOf(fakeAnswerUpdateParam().copy(answerId = cachedAnswerId))
                    )
                )
            )
        )

        interactor.updateQuestions(mapId, nodeId, testId, questions)

        val updateRequestCache = updateDataSource.get(mapId)?.value
        assertNotNull(updateRequestCache)
        assertNotNull(updateRequestCache.answers.updated.firstOrNull { it.answerId == cachedAnswerId })
        assertNotNull(updateRequestCache.questions.updated.firstOrNull { it.questionId == cachedQuestionId })
        assertNotNull(updateRequestCache.answers.insert.firstOrNull { it.answerId == "answerId1" })
        assertNull(updateRequestCache.questions.insert.firstOrNull { it.questionId == cachedQuestionId })
        assertTrue(updateRequestCache.answers.removed.isNotEmpty())
    }

    private suspend fun setupMap(mapId: String = "mapId") {
        val originalMap: SummaryMapResponseRemote = fakeSummaryEditMapResponseRemote()
        everySuspending { mindMapDataSource.fetch(isAny()) } returns originalMap
        interactor.getMap(mapId)
    }
}
