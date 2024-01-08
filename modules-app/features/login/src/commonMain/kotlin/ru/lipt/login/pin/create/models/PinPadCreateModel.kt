package ru.lipt.login.pin.create.models

import androidx.compose.runtime.Immutable

@Immutable
data class PinPadCreateModel(
    val pin: String = "",
    val pinRepeat: String = ""
) {

    val isButtonEnabled: Boolean = pin == pinRepeat
}
