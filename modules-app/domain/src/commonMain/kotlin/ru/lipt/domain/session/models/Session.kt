package ru.lipt.domain.session.models

import dev.icerock.moko.parcelize.IgnoredOnParcel
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val sessionId: String = "",
    val userId: String = "",
    val userEmail: String = "",
) {

    @IgnoredOnParcel
    val isEnabled = sessionId.isNotEmpty() && userId.isNotEmpty() && userEmail.isNotEmpty()
}
