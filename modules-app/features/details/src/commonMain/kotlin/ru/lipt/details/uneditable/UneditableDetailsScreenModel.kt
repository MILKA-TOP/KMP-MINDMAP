package ru.lipt.details.uneditable

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.LoadingState
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.idle
import ru.lipt.details.common.params.NodeDetailsScreenParams
import ru.lipt.details.uneditable.models.UneditableDetailsScreenUi
import ru.lipt.domain.map.MindMapInteractor
// import ru.lipt.domain.map.models.Node
import ru.lipt.testing.common.params.TestCompleteScreenParams

@Suppress("UnusedPrivateMember")
class UneditableDetailsScreenModel(
    private val params: NodeDetailsScreenParams,
    private val mapInteractor: MindMapInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<LoadingState<UneditableDetailsScreenUi, Unit>, NavigationTarget> =
        MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

//    private var _node: Node? = null

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    init {
        init()
    }

    fun onStarted() = init()

    fun init() {
//        screenModelScope.launchCatching(
//            catchBlock = {
//                _uiState.updateUi { Unit.error() }
//            }
//        ) {
//            _uiState.updateUi { loading() }
//
//            val node = mapInteractor.getNode(params.mapId, params.nodeId)
//            _node = node
//
//            val testResults = node.result
//
//            _uiState.updateUi {
//                UneditableDetailsScreenUi(
//                    text = node.description,
//                    testResult = when {
//                        testResults != null -> {
//                            UneditableTestResultUi.Result(
//                                resultLine = "Тест пройден: ${testResults.correctQuestionsCount} правильных ответов из ${testResults.questionsCount}",
//                                message = testResults.message,
//                            )
//                        }
//                        node.questions.isNotEmpty() -> UneditableTestResultUi.CompleteTest
//                        node.questions.isEmpty() -> UneditableTestResultUi.NoTest
//                        else -> UneditableTestResultUi.NoTest
//                    }
//                ).success()
//            }
//        }
    }

    fun onTestNavigateClick() = _uiState.navigateTo(
        NavigationTarget.CompleteTest(
            params = TestCompleteScreenParams(
                mapId = params.mapId,
                nodeId = params.nodeId,
            )
        )
    )

    fun onTestResultButtonClick() {
//        val result = _node?.result ?: return
//        _uiState.navigateTo(
//            NavigationTarget.TestResult(
//                params = TestingResultParams(result)
//            )
//        )
    } }
