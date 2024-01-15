package ru.lipt.details.uneditable.models

import androidx.compose.runtime.Immutable

@Immutable
data class UneditableDetailsScreenUi(
    val text: String = "",
    val testResult: UneditableTestResultUi = UneditableTestResultUi.CompleteTest,
)

@Immutable
sealed class UneditableTestResultUi {
    data object NoTest : UneditableTestResultUi()
    data object CompleteTest : UneditableTestResultUi()
    data class Result(
        val resultLine: String,
        val message: String? = null,
    ) : UneditableTestResultUi()
}
