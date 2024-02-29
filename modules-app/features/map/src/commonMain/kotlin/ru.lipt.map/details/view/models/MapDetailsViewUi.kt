package ru.lipt.map.details.view.models

import androidx.compose.runtime.Immutable

@Immutable
data class MapDetailsViewUi(
    val title: String,
    val description: String,
    val inviteUid: String,
    val admin: String,
    val enabledShowDeleteMap: Boolean = false,
    val dialog: Dialog? = null,
    val buttonIsEnabled: Boolean = false,
) {
    sealed class Dialog {
        data class HideMap(val inProgress: Boolean = false) : Dialog()
        data class RemoveMap(val inProgress: Boolean = false) : Dialog()
    }
}

@Immutable
data class UserUi(
    val id: String,
    val email: String,
)
