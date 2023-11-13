package ru.lipt.domain.map.models

data class MindNode(
    val id: String,
    val title: String,
    val childIds: List<String> = listOf(),
)
