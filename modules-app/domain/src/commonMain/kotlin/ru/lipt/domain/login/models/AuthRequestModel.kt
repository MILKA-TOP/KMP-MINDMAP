package ru.lipt.domain.login.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestModel(
    val email: String,
    val password: String,
)
