package ru.lipt.domain.map.models.update

import kotlinx.serialization.Serializable

@Serializable
data class MapsUpdateResponseRemote(
    val nodes: List<MapsKeyResponsePair> = emptyList(),
    val tests: List<MapsKeyResponsePair> = emptyList(),
    val questions: List<MapsKeyResponsePair> = emptyList(),
    val answers: List<MapsKeyResponsePair> = emptyList(),
) {
    val nodeIds: Map<String, String> = nodes.associate { it.deviceId to it.serverId }
    val testsIds: Map<String, String> = tests.associate { it.deviceId to it.serverId }
    val questionsIds: Map<String, String> = questions.associate { it.deviceId to it.serverId }
    val answersIds: Map<String, String> = answers.associate { it.deviceId to it.serverId }
}

@Serializable
data class MapsKeyResponsePair(val deviceId: String, val serverId: String)
