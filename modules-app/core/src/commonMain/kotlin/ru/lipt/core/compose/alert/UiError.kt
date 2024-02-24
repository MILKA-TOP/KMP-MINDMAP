package ru.lipt.core.compose.alert

import dev.icerock.moko.resources.StringResource

sealed class UiError {

    sealed class Alert(
        open val code: String = "",
        open val title: String? = null,
        open val titleRes: StringResource? = null,
        open val message: String? = null,
        open val messageRes: StringResource? = null,
        open val confirmText: String = "Ок",
    ) : UiError() {

        data class Default(
            override val code: String = "",
            override val title: String? = null,
            override val titleRes: StringResource? = null,
            override val message: String? = null,
            override val messageRes: StringResource? = null,
            override val confirmText: String = "Close",
        ) : Alert(
            title = title,
            message = message,
            confirmText = confirmText,
        )
    }

    data class Validation(
        val code: String = "",
        val message: String = "",
        val field: String = "",
        val isLocal: Boolean = false,
    ) : UiError()

    object Empty : UiError()
}
