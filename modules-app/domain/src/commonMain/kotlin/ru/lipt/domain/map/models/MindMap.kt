package ru.lipt.domain.map.models

import ru.lipt.domain.login.models.User

data class MindMap(
    val id: String,
    val admin: User,
    val title: String,
    val viewType: MapType,
    val description: String,
    val nodes: List<Node>
)
