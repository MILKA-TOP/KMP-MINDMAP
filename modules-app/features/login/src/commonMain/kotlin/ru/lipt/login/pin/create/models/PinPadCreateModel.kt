package ru.lipt.login.pin.create.models

import androidx.compose.runtime.Immutable
import ru.lipt.login.pin.extensions.PIN_SIZE

@Immutable
data class PinPadCreateModel(
    val pin: String = "",
    val pinRepeat: String = "",
    val isButtonInProgress: Boolean = false,
) {

    val isPinEnabled: Boolean = pin == pinRepeat && pin.isNotEmpty() && pin.length == PIN_SIZE
}
