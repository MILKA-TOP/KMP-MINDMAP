package ru.lipt.details.editable.models

import androidx.compose.runtime.Immutable

@Immutable
data class EditableDetailsScreenUi(
    val text: String = "",
    val testResult: EditableTestResultUi = EditableTestResultUi.NoTest,
)

@Immutable
sealed class EditableTestResultUi {
    data object NoTest : EditableTestResultUi()
    data object EditTest : EditableTestResultUi()
}
