package ru.lipt.domain.session.models

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val sessionId: String = "",
    val userId: String = "",
    val userEmail: String = "",
) {

    val isEnabled: Boolean = sessionId.isNotEmpty() && userId.isNotEmpty() && userEmail.isNotEmpty()
}
