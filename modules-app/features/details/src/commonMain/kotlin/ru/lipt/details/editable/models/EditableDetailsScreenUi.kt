package ru.lipt.details.editable.models

import androidx.compose.runtime.Immutable

@Immutable
data class EditableDetailsScreenUi(
    val title: String = "",
    val description: String = "",
    val testResult: EditableTestResultUi = EditableTestResultUi.NoTest,
    val isRootNode: Boolean = false,
    val isSaveButtonEnabled: Boolean = false,
    val alertUi: Alert? = null,
) {
    sealed class Alert {
        data class RemoveAlertUi(val parentTitle: String) : Alert()
        data object BackAndSave : Alert()
        data object NextAndSave : Alert()
    }
}

@Immutable
sealed class EditableTestResultUi {
    data object NoTest : EditableTestResultUi()
    data object EditTest : EditableTestResultUi()
}
