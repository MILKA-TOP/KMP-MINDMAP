package ru.lipt.details.editable

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
import ru.lipt.details.editable.models.EditableDetailsScreenUi
import ru.lipt.details.editable.models.EditableTestResultUi
import ru.lipt.domain.map.IMindMapInteractor
import ru.lipt.domain.map.models.NodesEditResponseRemote
import ru.lipt.domain.map.models.SummaryEditMapResponseRemote
import ru.lipt.testing.common.params.TestEditScreenParams

class EditableDetailsScreenModel(
    val params: NodeDetailsScreenParams,
    private val mapInteractor: IMindMapInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<LoadingState<EditableDetailsScreenUi, Unit>, NavigationTarget> =
        MutableScreenUiStateFlow(idle())
    val uiState = _uiState.asStateFlow()

    private var _map: SummaryEditMapResponseRemote? = null
    private var _node: NodesEditResponseRemote? = null
    private var _parentNode: NodesEditResponseRemote? = null
    private var _initTitle: String = ""
    private var _currentTitle: String = ""
    private var _initDescription: String = ""
    private var _currentDescription: String = ""

    private val _isDataWasEditable: Boolean
        get() = (_initTitle != _currentTitle || _initDescription != _currentDescription)
                && _currentTitle.isNotEmpty()

    init {
        init()
    }

    fun onStarted() = init()

    fun init() {
        screenModelScope.launchCatching(catchBlock = {
            _uiState.updateUi { Unit.error() }
        }) {
            _uiState.updateUi { loading() }
            val map = mapInteractor.getMap(params.mapId) as SummaryEditMapResponseRemote
            val node = mapInteractor.getEditableNode(params.mapId, params.nodeId)
            val isRootNode = node.parentNodeId == null
            node.parentNodeId?.let { parentNodeId ->
                _parentNode = mapInteractor.getEditableNode(params.mapId, parentNodeId)
            }
            _node = node
            _map = map
            _initTitle = map.title.takeIf { isRootNode } ?: node.label
            _currentTitle = _initTitle
            _initDescription = node.description
            _currentDescription = _initDescription
            _uiState.updateUi {
                EditableDetailsScreenUi(
                    title = _initTitle,
                    description = _initDescription,
                    isRootNode = node.parentNodeId == null,
                    testResult = if (node.test == null) EditableTestResultUi.NoTest else EditableTestResultUi.EditTest
                ).success()
            }
        }
    }

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    fun onEditTitleText(text: String) {
        _currentTitle = text.trimStart()
        _uiState.updateUi { copy { it.copy(title = text.trimStart()).validateSaveButton() } }
    }

    fun onEditDescriptionText(text: String) {
        _currentDescription = text.trimStart()
        _uiState.updateUi { copy { it.copy(description = text.trimStart()).validateSaveButton() } }
    }

    fun onSaveButtonClick() {
        saveAction {
            _uiState.navigateTo(NavigationTarget.SuccessSave)
        }
    }

    fun onBackConfirmAlertButtonClick() {
        saveAction {
            _uiState.navigateTo(NavigationTarget.NavigateUp)
        }
    }

    fun onNextConfirmAlertButtonClick() {
        saveAction {
            onTestNavigate()
        }
    }

    fun onTestEditButtonClick() {
        if (_isDataWasEditable) {
            _uiState.updateUi { copy { it.copy(alertUi = EditableDetailsScreenUi.Alert.NextAndSave) } }
        } else {
            onTestNavigate()
        }
    }

    fun onNavigateUpClick() {
        if (_isDataWasEditable) {
            _uiState.updateUi { copy { it.copy(alertUi = EditableDetailsScreenUi.Alert.BackAndSave) } }
        } else {
            _uiState.navigateTo(NavigationTarget.NavigateUp)
        }
    }

    private fun EditableDetailsScreenUi.validateSaveButton() = copy(
        isSaveButtonEnabled = _isDataWasEditable
    )

    fun onRemoveButtonClick() {
        val parentNodeTitle = _map?.title?.takeIf { _parentNode?.id == null } ?: _parentNode?.label.orEmpty()
        _uiState.updateUi { copy { it.copy(alertUi = EditableDetailsScreenUi.Alert.RemoveAlertUi(parentNodeTitle)) } }
    }

    fun onAlertClose() {
        _uiState.updateUi { copy { it.copy(alertUi = null) } }
    }

    fun onRemoveAlertConfirm() {
        val mapId = _map?.id ?: return
        val nodeId = _node?.id ?: return
        screenModelScope.launchCatching(
            finalBlock = {
                _uiState.updateUi { copy { it.copy(alertUi = null) } }
            }
        ) {
            mapInteractor.removeNode(mapId, nodeId)
            _uiState.navigateTo(NavigationTarget.SuccessRemove)
        }
    }

    private fun onTestNavigate() {
        _uiState.navigateTo(
            NavigationTarget.EditTest(
                TestEditScreenParams(
                    mapId = params.mapId,
                    nodeId = params.nodeId,
                    testId = _node?.test?.id
                )
            )
        )
    }

    private fun saveAction(onSuccessAction: () -> Unit) {
        val mapId = _map?.id ?: return
        val nodeId = _node?.id ?: return
        val title = _currentTitle.trim()
        val description = _currentDescription.trim()
        screenModelScope.launchCatching {
            mapInteractor.saveNodeData(mapId, nodeId, title, description)
            _initTitle = title
            _currentTitle = _initTitle
            _initDescription = description
            _currentDescription = _initDescription

            _uiState.updateUi { copy { it.copy(title = title, description = description).validateSaveButton() } }
            onSuccessAction()
        }
    }
}
