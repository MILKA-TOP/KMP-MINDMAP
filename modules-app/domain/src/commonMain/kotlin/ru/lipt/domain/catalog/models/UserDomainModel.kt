package ru.lipt.domain.catalog.models

import kotlinx.serialization.Serializable

@Serializable
class UserDomainModel(
    val id: String,
    val email: String,
)
