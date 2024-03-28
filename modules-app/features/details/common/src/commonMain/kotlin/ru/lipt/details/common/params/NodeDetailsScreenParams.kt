package ru.lipt.details.common.params

data class NodeDetailsScreenParams(
    val mapId: String,
    val nodeId: String,
    val otherUserId: String? = null,
)
