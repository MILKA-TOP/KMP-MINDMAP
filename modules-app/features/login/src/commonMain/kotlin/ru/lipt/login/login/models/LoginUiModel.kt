package ru.lipt.login.login.models

import androidx.compose.runtime.Immutable

@Immutable
data class LoginUiModel(
    val email: String = "",
    val password: String = "",
    val loginButtonEnable: Boolean = false,
    val buttonInProgress: Boolean = false,
)
