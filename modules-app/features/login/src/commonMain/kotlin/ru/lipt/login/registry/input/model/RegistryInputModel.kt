package ru.lipt.login.registry.input.model

import androidx.compose.runtime.Immutable

@Immutable
data class RegistryInputModel(
    val email: String = "",
    val password: String = "",
    val passwordRepeat: String = "",
    val registryButtonEnable: Boolean = false,
)
