package ru.lipt.login.registry.input

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.validate.isEmailValid
import ru.lipt.domain.login.LoginInteractor
import ru.lipt.login.registry.input.model.RegistryInputModel

class RegistryInputScreenModel(
    private val loginInteractor: LoginInteractor,
) : ScreenModel {

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
        val ui = _uiState.ui
        screenModelScope.launchCatching(
            catchBlock = {
                _uiState.showAlertError(
                    UiError.Alert.Default(
                        title = "Ошибка регистрации",
                        message = "Возникла какая-то ошибка при регистрации"
                    )
                )
            }
        ) {
            loginInteractor.register(email = ui.email.trim(), password = ui.password)

            _uiState.navigateTo(NavigationTarget.PinCreateNavigate)
        }
    }

    private fun RegistryInputModel.updateValidateState() = copy(registryButtonEnable = validate())

    private fun RegistryInputModel.validate(): Boolean =
        this.email.trim().isEmailValid()
                && password.isNotBlank()
                && passwordRepeat.isNotBlank()
                && password == passwordRepeat
}
