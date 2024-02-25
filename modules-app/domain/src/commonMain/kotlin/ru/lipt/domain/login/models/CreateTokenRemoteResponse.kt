package ru.lipt.domain.login.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateTokenRemoteResponse(
    val token: String
)
