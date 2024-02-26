package ru.lipt.catalog.create

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.catalog.common.params.CreateMindMapParams
import ru.lipt.catalog.create.models.CreateMindMapModel
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.domain.catalog.CatalogInteractor
import ru.lipt.map.common.params.MapScreenParams

class CreateMindMapScreenModel(
    params: CreateMindMapParams,
    private val catalogInteractor: CatalogInteractor,
) : ScreenModel {

    private val referralParams: CreateMindMapParams.Referral? = (params as? CreateMindMapParams.Referral)

    private val _uiState: MutableScreenUiStateFlow<CreateMindMapModel, NavigationTarget> =
        MutableScreenUiStateFlow(
            CreateMindMapModel(
                referralParams = referralParams?.title,
                title = referralParams?.title.orEmpty(),
                description = referralParams?.description.orEmpty(),
            ).updateValidateState()
        )
    val uiState = _uiState.asStateFlow()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onTitleTextChanged(text: String) {
        _uiState.updateUi { copy(title = text.trimStart()).updateValidateState() }
    }

    fun onDescriptionTextChanged(text: String) {
        _uiState.updateUi { copy(description = text.trimStart()) }
    }

    fun onPasswordTextChanged(text: String) {
        _uiState.updateUi { copy(password = text.filter { !it.isWhitespace() }).updateValidateState() }
    }

    fun onEnabledPasswordCheckboxClick(boolean: Boolean) {
        _uiState.updateUi { copy(enabledPassword = boolean).updateValidateState() }
    }

    fun onButtonClick() {
        screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(UiError.Alert.Default(message = throwable.message))
            },
            finalBlock = {
                _uiState.updateUi { copy(buttonInProgress = false) }
            }
        ) {
            _uiState.updateUi { copy(buttonInProgress = true) }
            val ui = _uiState.ui
            if (!ui.validate()) throw IllegalArgumentException()

            val mapId = catalogInteractor.createMap(
                title = ui.title.trim(),
                description = ui.description.trimStart(),
                password = ui.password.trim().takeIf { ui.enabledPassword },
                mapRefId = referralParams?.mapId,
            )

            _uiState.navigateTo(
                NavigationTarget.MindMapScreen(
                    params = MapScreenParams(id = mapId)
                )
            )
        }
    }

    private fun CreateMindMapModel.updateValidateState() = copy(createButtonEnabled = validate())

    private fun CreateMindMapModel.validate(): Boolean =
        this.title.trim().isNotEmpty()
                && (enabledPassword && password.length >= PASSWORD_SIZE || !enabledPassword)

    companion object {
        private const val PASSWORD_SIZE = 8
    }
}
