package ru.lipt.testing.edit

sealed class NavigationTarget {
    data object SuccessQuestionsSave : NavigationTarget()

    data class OpenQuestions(val position: Int) : NavigationTarget()
}
