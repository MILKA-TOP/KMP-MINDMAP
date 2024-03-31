package ru.lipt.catalog.migrate

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.darkrockstudios.libraries.mpfilepicker.MPFile
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.catalog.migrate.models.MigrateMindMapModel
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.uri.uri
import ru.lipt.domain.catalog.CatalogInteractor
import ru.lipt.map.common.params.MapScreenParams

class MigrateMindMapScreenModel(
    private val catalogInteractor: CatalogInteractor,
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<MigrateMindMapModel, NavigationTarget> =
        MutableScreenUiStateFlow(
            MigrateMindMapModel().updateValidateState()
        )
    val uiState = _uiState.asStateFlow()

    private var _file: MPFile<Any>? = null

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onPasswordTextChanged(text: String) {
        _uiState.updateUi { copy(password = text.filter { !it.isWhitespace() }).updateValidateState() }
    }

    fun onEnabledPasswordCheckboxClick(boolean: Boolean) {
        _uiState.updateUi { copy(enabledPassword = boolean).updateValidateState() }
    }

    fun onFilePickerClick() {
        _uiState.updateUi { copy(showFilePicker = true) }
    }

    fun onButtonClick() {
        val file = _file ?: return
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
            val text = file.getFileByteArray().decodeToString()
            val mapId = catalogInteractor.migrate(
                text = text,
                password = ui.password.trim().takeIf { ui.enabledPassword }
            )

            _uiState.navigateTo(
                NavigationTarget.MindMapScreen(
                    params = MapScreenParams(id = mapId)
                )
            )
        }
    }

    private fun MigrateMindMapModel.updateValidateState() = copy(createButtonEnabled = validate())

    private fun MigrateMindMapModel.validate(): Boolean =
        _file != null
                && (enabledPassword && password.length >= PASSWORD_SIZE || !enabledPassword)

    fun onFileSelected(mpFile: MPFile<Any>?) {
        _file = mpFile
        _uiState.updateUi { copy(showFilePicker = false, selectedFileTitle = mpFile?.path?.uri()?.lastPathSegment).updateValidateState() }
    }

    companion object {
        private const val PASSWORD_SIZE = 8
    }
}
