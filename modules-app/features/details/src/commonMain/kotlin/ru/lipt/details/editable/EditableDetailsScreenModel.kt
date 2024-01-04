package ru.lipt.details.editable

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.details.common.params.NodeDetailsScreenParams
import ru.lipt.details.editable.models.EditableDetailsScreenUi

class EditableDetailsScreenModel(
    val params: NodeDetailsScreenParams
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<EditableDetailsScreenUi, NavigationTarget> =
        MutableScreenUiStateFlow(EditableDetailsScreenUi())
    val uiState = _uiState.asStateFlow()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    fun onEditText(text: String) {
        _uiState.updateUi { copy(text = text) }
    }

    fun onTextSaveButtonClick() {
        _uiState.navigateTo(NavigationTarget.SaveText)
    }

    fun onEditTestClick() {
        _uiState.navigateTo(
            NavigationTarget.EditTest(
                NodeDetailsScreenParams(
                    mapId = params.mapId,
                    nodeId = params.nodeId,
                )
            )
        )
    }
}
