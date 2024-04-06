package ru.lipt.login.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.asStateFlow
import ru.lipt.core.compose.MutableScreenUiStateFlow
import ru.lipt.core.compose.alert.UiError
import ru.lipt.core.coroutines.launchCatching
import ru.lipt.core.validate.isEmailValid
import ru.lipt.domain.login.IUnAuthedLoginInteractor
import ru.lipt.login.login.models.LoginUiModel

class LoginScreenModel(
    private val loginInteractor: IUnAuthedLoginInteractor
) : ScreenModel {

    private val _uiState: MutableScreenUiStateFlow<LoginUiModel, NavigationTarget> =
        MutableScreenUiStateFlow(LoginUiModel())
    val uiState = _uiState.asStateFlow()

    fun handleNavigation(navigate: (NavigationTarget) -> Unit) = _uiState.handleNavigation(navigate)

    fun handleErrorAlertClose() = _uiState.handleErrorAlertClose()

    fun onEmailTextChanged(text: String) {
        _uiState.updateUi {
            copy(email = text.trim())
                .updateValidateState()
        }
    }

    fun onPasswordTextChanged(text: String) {
        _uiState.updateUi {
            copy(password = text.trim())
                .updateValidateState()
        }
    }

    fun onLoginButtonClick() {
        val ui = _uiState.ui
        screenModelScope.launchCatching(
            catchBlock = { throwable ->
                _uiState.showAlertError(
                    UiError.Alert.Default(
                        message = throwable.message
                    )
                )
            },
            finalBlock = {
                _uiState.updateUi { copy(buttonInProgress = false) }
            }
        ) {
            _uiState.updateUi { copy(buttonInProgress = true) }
            loginInteractor.enterAuthData(email = ui.email.trim(), password = ui.password.trim())

            _uiState.navigateTo(NavigationTarget.PinCreateNavigate)
        }
    }

    private fun LoginUiModel.updateValidateState() = copy(loginButtonEnable = validate())

    private fun LoginUiModel.validate(): Boolean =
        this.email.trim().isEmailValid()
                && password.isNotBlank()
}
