package ru.lipt.testing.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.testing.MR
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
            TableFieldModel.QuestionResultHeader(model.questionText, model.isCorrectQuestion),
        )
        Spacer(modifier = Modifier.height(16.dp))
        model.allAnswers.map { item ->
            TableField(model = item)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (!model.isCorrectQuestion) {
            TableField(
                TableFieldModel.Caption(stringResource(MR.strings.test_correct_answers_text)),
            )
            Spacer(modifier = Modifier.height(8.dp))
            model.correctAnswers.map { item ->
                TableField(
                    model = item,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(4.dp))
    }
}
