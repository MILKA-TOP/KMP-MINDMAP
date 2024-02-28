package ru.lipt.domain.map.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseRemote(
    val id: String,
    val email: String,
)
