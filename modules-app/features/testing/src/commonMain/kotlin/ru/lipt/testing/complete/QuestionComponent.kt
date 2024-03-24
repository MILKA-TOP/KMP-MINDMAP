package ru.lipt.testing.complete

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.lipt.testing.complete.models.QuestionUiModel
import ru.lipt.testing.edit.question.base.TableField
import ru.lipt.testing.edit.question.base.models.TableFieldModel

@Composable
fun QuestionCompleteComponent(
    model: QuestionUiModel,
    onSingleCheckboxSelect: (Int) -> Unit = {},
    onMultipleCheckboxSelect: (Int, Boolean) -> Unit = { _, _ -> },
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        TableField(
            TableFieldModel.Header(model.questionText),
        )
        Spacer(modifier = Modifier.height(32.dp))

        model.answers.forEachIndexed { position, item ->
            TableField(
                model = item,
                onSingleCheckboxSelect = { onSingleCheckboxSelect(position) },
                onMultipleCheckboxSelect = { onMultipleCheckboxSelect(position, it) },
            )
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
