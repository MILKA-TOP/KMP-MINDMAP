package ru.lipt.domain.login.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestRemote(
    val userId: String,
    val pinToken: String,
)
