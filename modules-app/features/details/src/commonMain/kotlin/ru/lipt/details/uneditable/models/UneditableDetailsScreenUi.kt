package ru.lipt.details.uneditable.models

import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.details.MR

@Immutable
data class UneditableDetailsScreenUi(
    val title: String = "",
    val description: String = "",
    val links: List<DescriptionLink> = emptyList(),
    val isNodeMarked: Boolean = false,
    val isButtonInProgress: Boolean = false,
    val isButtonEnabled: Boolean = true,
    val testResult: UneditableTestResultUi = UneditableTestResultUi.NoTest,
) {

    val descriptionAnnotatedString: AnnotatedString
        @Composable @ReadOnlyComposable get() = buildAnnotatedString {
            append(description)
            links.map { link ->
                addStyle(
                    style = MindTheme.typography.link1.toSpanStyle(), start = link.start, end = link.end
                )

                addStringAnnotation(
                    tag = "URL", annotation = link.link, start = link.start, end = link.end
                )
            }
        }

    val buttonColor: ButtonColors
        @Composable get() = if (isNodeMarked) ButtonDefaults.buttonColors()
        else ButtonDefaults.outlinedButtonColors(
            backgroundColor = MindTheme.colors.unmarkedNode, contentColor = MindTheme.colors.material.onBackground
        )

    val buttonText: String
        @Composable get() = if (isNodeMarked) stringResource(MR.strings.node_text_for_marked_node_button_text)
        else stringResource(MR.strings.node_text_for_unmarked_node_button_text)
}

@Immutable
data class DescriptionLink(val start: Int, val end: Int, val link: String)

@Immutable
sealed class UneditableTestResultUi {
    data object NoTest : UneditableTestResultUi()
    data object CompleteTest : UneditableTestResultUi()
    data class Result(
        val correctAnswers: Int = 0,
        val answersCount: Int = 0,
        val message: String? = null,
    ) : UneditableTestResultUi()
}
