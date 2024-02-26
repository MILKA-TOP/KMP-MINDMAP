package ru.lipt.domain.catalog.models

import kotlinx.serialization.Serializable

@Serializable
data class CatalogMindMap(
    val id: String,
    val title: String,
    val admin: UserDomainModel,
    val description: String,
    val isEnableEdit: Boolean,
    val isSaved: Boolean,
    val isPrivate: Boolean,
)
