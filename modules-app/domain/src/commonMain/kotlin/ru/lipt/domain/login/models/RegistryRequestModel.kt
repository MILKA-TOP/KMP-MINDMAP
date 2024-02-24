package ru.lipt.domain.login.models

import kotlinx.serialization.Serializable

@Serializable
class RegistryRequestModel(
    val email: String,
    val password: String,
)
