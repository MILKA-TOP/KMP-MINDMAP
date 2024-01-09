package ru.lipt.login.pin.input.model

import androidx.compose.runtime.Immutable

@Immutable
data class PinPadInputModel(
    val pin: String = "",
) {

    val isPinEnabled: Boolean = pin.isNotEmpty()
}
