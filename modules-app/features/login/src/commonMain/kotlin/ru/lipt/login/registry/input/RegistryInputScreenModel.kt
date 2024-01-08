package ru.lipt.login.registry.input

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.validate.isEmailValid
import ru.lipt.login.registry.input.model.RegistryInputModel

class RegistryInputScreenModel : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<RegistryInputModel, NavigationTarget> =
        MutableScreenUiStateFlow(RegistryInputModel())
    val uiState = _uiState.asStateFlow()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onEmailTextChanged(text: String) {
        _uiState.updateUi {
            copy(email = text)
                .updateValidateState()
        }
    }

    fun onPasswordTextChanged(text: String) {
        _uiState.updateUi {
            copy(password = text)
                .updateValidateState()
        }
    }

    fun onPasswordRepeatTextChanged(text: String) {
        _uiState.updateUi {
            copy(passwordRepeat = text)
                .updateValidateState()
        }
    }

    fun onRegistryButtonClick() {
        _uiState.navigateTo(NavigationTarget.PinCreateNavigate)
    }

    private fun RegistryInputModel.updateValidateState() = copy(registryButtonEnable = validate())

    private fun RegistryInputModel.validate(): Boolean =
        this.email.isEmailValid()
                && password.isNotBlank()
                && passwordRepeat.isNotBlank()
                && password == passwordRepeat
}
