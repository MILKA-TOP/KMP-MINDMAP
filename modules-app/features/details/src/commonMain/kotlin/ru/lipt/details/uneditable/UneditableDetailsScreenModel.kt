package ru.lipt.details.uneditable

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.LoadingState
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.error
import ru.lipt.core.idle
import ru.lipt.core.loading
import ru.lipt.core.success
import ru.lipt.details.common.params.NodeDetailsScreenParams
import ru.lipt.details.uneditable.models.UneditableDetailsScreenUi
import ru.lipt.details.uneditable.models.UneditableTestResultUi
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.domain.map.models.Node
import ru.lipt.testing.common.params.TestCompleteScreenParams

class UneditableDetailsScreenModel(
    private val params: NodeDetailsScreenParams,
    private val mapInteractor: MindMapInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<LoadingState<UneditableDetailsScreenUi, Unit>, NavigationTarget> =
        MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

    private var _node: Node? = null

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)
    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    init {
        init()
    }

    fun init() {
        screenModelScope.launchCatching(
            catchBlock = {
                _uiState.updateUi { Unit.error() }
            }
        ) {
            _uiState.updateUi { loading() }

            val node = mapInteractor.getNode(params.mapId, params.nodeId)
            _node = node

            val testResults = node.result

            _uiState.updateUi {
                UneditableDetailsScreenUi(
                    text = node.description,
                    testResult = when {
                        testResults != null -> {
                            UneditableTestResultUi.Result(
                                resultLine = "Тест пройден: ${testResults.correctQuestionsCount} правильных ответов из ${testResults.questionsCount}",
                                message = testResults.message,
                            )
                        }
                        node.questions.isNotEmpty() -> UneditableTestResultUi.CompleteTest
                        node.questions.isEmpty() -> UneditableTestResultUi.NoTest
                        else -> UneditableTestResultUi.NoTest
                    }
                ).success()
            }
        }
    }

    fun onTestNavigateClick() = _uiState.navigateTo(
        NavigationTarget.CompleteTest(
            params = TestCompleteScreenParams(
                mapId = params.mapId,
                nodeId = params.nodeId,
            )
        )
    )

    fun onTestResultButtonClick() = Unit
}
