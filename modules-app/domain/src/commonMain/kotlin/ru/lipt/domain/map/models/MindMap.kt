package ru.lipt.domain.map.models

data class MindMap(
    val id: String,
    val nodes: List<MindNode> = listOf(),
)
