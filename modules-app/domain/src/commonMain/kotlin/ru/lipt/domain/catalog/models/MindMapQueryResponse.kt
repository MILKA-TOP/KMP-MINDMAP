package ru.lipt.domain.catalog.models

data class MindMapQueryResponse(
    val id: String,
    val title: String,
    val description: String,
    val isNeedPassword: Boolean,
)
