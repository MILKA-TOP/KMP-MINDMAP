package ru.lipt.details.editable.models

import androidx.compose.runtime.Immutable

@Immutable
data class EditableDetailsScreenUi(
    val title: String = "",
    val description: String = "",
    val testResult: EditableTestResultUi = EditableTestResultUi.NoTest,
    val isRootNode: Boolean = false,
    val isSaveButtonEnabled: Boolean = false,
    val remoevAlertUi: RemoveAlertUi? = null,
)

@Immutable
sealed class EditableTestResultUi {
    data object NoTest : EditableTestResultUi()
    data object EditTest : EditableTestResultUi()
}

@Immutable
data class RemoveAlertUi(val parentTitle: String)
