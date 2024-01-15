package ru.lipt.map.details.models

import androidx.compose.runtime.Immutable

@Immutable
data class MapDetailsUi(
    val title: String,
    val description: String,
    val inviteUid: String,
    val admin: String,
    val users: List<UserUi> = emptyList(),
    val enabledShowDeleteMap: Boolean = false,
    val dialog: Dialog? = null,
) {
    sealed class Dialog {
        data class UserMap(val userId: String, val email: String) : Dialog()
        data class DeleteMap(val inProgress: Boolean = false) : Dialog()
    }
}

@Immutable
data class UserUi(
    val id: String,
    val email: String,
)
