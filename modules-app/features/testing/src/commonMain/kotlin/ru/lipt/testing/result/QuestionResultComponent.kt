package ru.lipt.testing.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.lipt.testing.edit.question.base.TableField
import ru.lipt.testing.edit.question.base.models.TableFieldModel
import ru.lipt.testing.result.models.QuestionResultUiModel

@Composable
fun QuestionResultComponent(
    model: QuestionResultUiModel,
) {

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentSize(unbounded = false),
    ) {
        TableField(
            TableFieldModel.Header(model.questionText),
        )
        model.allAnswers.map { item ->
            TableField(
                model = item,
            )
        }
        TableField(
            TableFieldModel.Caption("Правильный ответ"),
        )
        model.correctAnswers.map { item ->
            TableField(
                model = item,
            )
        }
    }
}
