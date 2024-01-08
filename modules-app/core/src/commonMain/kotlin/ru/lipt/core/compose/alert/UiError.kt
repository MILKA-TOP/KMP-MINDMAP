package ru.lipt.core.compose.alert

sealed class UiError {

    sealed class Alert(
        open val code: String = "",
        open val title: String? = null,
        open val message: String,
        open val confirmText: String = "Ок",
    ) : UiError() {

        data class Default(
            override val code: String = "",
            override val title: String? = null,
            override val message: String,
            override val confirmText: String = "Закрыть",
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
