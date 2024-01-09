package ru.lipt.domain.session.models

data class Session(
    val sessionId: String = "",
    val userId: String = "",
    val userEmail: String = "",
) {
    val isEnabled = sessionId.isNotEmpty() && userId.isNotEmpty() && userEmail.isNotEmpty()
}
