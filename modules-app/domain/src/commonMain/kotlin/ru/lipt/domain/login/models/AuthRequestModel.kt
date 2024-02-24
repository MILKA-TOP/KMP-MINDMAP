package ru.lipt.domain.login.models

import kotlinx.serialization.Serializable

@Serializable
class AuthRequestModel(
    val email: String,
    val password: String,
)
