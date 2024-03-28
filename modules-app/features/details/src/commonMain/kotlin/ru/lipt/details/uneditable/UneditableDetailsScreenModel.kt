package ru.lipt.details.uneditable

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.LoadingState
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.error
import ru.lipt.core.idle
import ru.lipt.core.loading
import ru.lipt.core.success
import ru.lipt.details.common.params.NodeDetailsScreenParams
import ru.lipt.details.uneditable.models.DescriptionLink
import ru.lipt.details.uneditable.models.UneditableDetailsScreenUi
import ru.lipt.details.uneditable.models.UneditableTestResultUi
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.domain.map.models.NodesViewResponseRemote
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote
import ru.lipt.testing.common.params.TestCompleteScreenParams
import ru.lipt.testing.common.params.TestingResultParams

@Suppress("UnusedPrivateMember")
class UneditableDetailsScreenModel(
    private val params: NodeDetailsScreenParams,
    private val mapInteractor: MindMapInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<LoadingState<UneditableDetailsScreenUi, Unit>, NavigationTarget> =
        MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

    private var _node: NodesViewResponseRemote? = null
    private val isMapViewType = params.otherUserId != null

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    init {
        init()
    }

    fun onStarted() = init()

    fun init() {
        screenModelScope.launchCatching(catchBlock = {
            _uiState.updateUi { Unit.error() }
        }) {
            _uiState.updateUi { loading() }

            val map = getMap()
            val node = map.nodes.first { it.id == params.nodeId }
            _node = node
            val test = node.test
            val result = test?.testResult

            _uiState.updateUi {
                UneditableDetailsScreenUi(title = node.label.takeIf { node.parentNodeId != null } ?: map.title,
                    description = node.description,
                    links = highlightLinkPositions(node.description),
                    isNodeMarked = node.isSelected,
                    isButtonEnabled = !isMapViewType,
                    testResult = when {
                        result != null -> {
                            UneditableTestResultUi.Result(
                                correctAnswers = result.correctQuestionsCount,
                                answersCount = result.completedQuestions.size,
                                message = result.message,
                            )
                        }
                        node.test?.questions.orEmpty().isNotEmpty() && !isMapViewType -> UneditableTestResultUi.CompleteTest
                        else -> UneditableTestResultUi.NoTest
                    }).success()
            }
        }
    }

    fun onMarkButtonClick() {
        screenModelScope.launchCatching(catchBlock = { throwable ->
            _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
        }, finalBlock = {
            _uiState.updateUi { copy { it.copy(isButtonInProgress = false) } }
        }) {
            _uiState.updateUi { copy { it.copy(isButtonInProgress = true) } }
            val bool = mapInteractor.toggleNode(params.mapId, params.nodeId)
            _uiState.updateUi { copy { it.copy(isNodeMarked = bool) } }
        }
    }

    fun onTestNavigateClick() = _uiState.navigateTo(
        NavigationTarget.CompleteTest(
            params = TestCompleteScreenParams(
                mapId = params.mapId,
                nodeId = params.nodeId,
                testId = _node?.test?.id!!
            )
        )
    )

    fun onTestResultButtonClick() {
        val result = _node?.test?.testResult ?: return
        _uiState.navigateTo(
            NavigationTarget.TestResult(
                params = TestingResultParams(result)
            )
        )
    }

    private suspend fun getMap(): SummaryViewMapResponseRemote {
        return params.otherUserId?.let { userId ->
            mapInteractor.fetchViewMap(params.mapId, userId)
        } ?: mapInteractor.getMap(params.mapId) as SummaryViewMapResponseRemote
    }

    private fun highlightLinkPositions(input: String): List<DescriptionLink> {
        val urlRegex = "https?://[^\\s]+".toRegex()
        val matches = urlRegex.findAll(input)

        return matches.map {
            DescriptionLink(
                start = it.range.start, end = it.range.endInclusive, link = it.value
            )
        }.toList()
    }
}
