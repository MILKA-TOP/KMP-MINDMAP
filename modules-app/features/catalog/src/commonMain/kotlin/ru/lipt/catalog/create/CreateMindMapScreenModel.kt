package ru.lipt.catalog.create

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.catalog.create.models.CreateMindMapModel
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.domain.catalog.CatalogInteractor
import ru.lipt.map.common.params.MapScreenParams

class CreateMindMapScreenModel(
    private val catalogInteractor: CatalogInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<CreateMindMapModel, NavigationTarget> =
        MutableScreenUiStateFlow(CreateMindMapModel())
    val uiState = _uiState.asStateFlow()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onTitleTextChanged(text: String) {
        _uiState.updateUi { copy(title = text).updateValidateState() }
    }

    fun onDescriptionTextChanged(text: String) {
        _uiState.updateUi { copy(description = text) }
    }

    fun onPasswordTextChanged(text: String) {
        _uiState.updateUi { copy(password = text).updateValidateState() }
    }

    fun onEnabledPasswordCheckboxClick(boolean: Boolean) {
        _uiState.updateUi { copy(enabledPassword = boolean).updateValidateState() }
    }

    fun onButtonClick() {
        screenModelScope.launchCatching(
            catchBlock = {
                _uiState.showAlertError(UiError.Alert.Default(message = "Creating map error"))
            }
        ) {
            val ui = _uiState.ui
            if (!ui.validate()) throw IllegalArgumentException()

            val map = catalogInteractor.createMap(
                title = ui.title,
                description = ui.description,
                password = ui.password.takeIf { ui.enabledPassword }
            )

            _uiState.navigateTo(
                NavigationTarget.MindMapScreen(
                    params = MapScreenParams(id = map.id)
                )
            )
        }
    }

    private fun CreateMindMapModel.updateValidateState() = copy(createButtonEnabled = validate())

    private fun CreateMindMapModel.validate(): Boolean =
        this.title.trim().isNotEmpty()
                && (enabledPassword && password.isNotEmpty() || !enabledPassword)
}
