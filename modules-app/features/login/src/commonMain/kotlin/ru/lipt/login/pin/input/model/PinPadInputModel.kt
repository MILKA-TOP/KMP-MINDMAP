package ru.lipt.login.pin.input.model

import androidx.compose.runtime.Immutable
import ru.lipt.login.pin.extensions.PIN_SIZE

@Immutable
data class PinPadInputModel(
    val pin: String = "",
    val isSetButtonInProgress: Boolean = false,
    val isLogOutButtonInProgress: Boolean = false,
    val showLogOutAlert: Boolean = false,
) {

    val isSetPinButtonEnabled: Boolean = pin.length == PIN_SIZE && !isLogOutButtonInProgress
    val isLogOutButtonEnabled: Boolean = !isSetButtonInProgress
}
