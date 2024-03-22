package ru.lipt.testing.edit.question

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.lipt.testing.edit.question.base.TableField
import ru.lipt.testing.edit.question.base.models.FieldTypes
import ru.lipt.testing.edit.question.base.models.TableFieldModel

@Composable
fun QuestionEditComponent(
    model: QuestionEditModel,
    modifier: Modifier = Modifier,
    onSingleCheckboxSelect: (Int) -> Unit = {},
    onMultipleCheckboxSelect: (Int, Boolean) -> Unit = { _, _ -> },
    onNewItemClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onHeaderTextChanged: (String) -> Unit = { _ -> },
    onFieldTextChanged: (Int, String) -> Unit = { _, _ -> },
    updateFieldType: (FieldTypes) -> Unit = { _ -> },
) {

    Column(
        modifier = Modifier.fillMaxWidth().then(modifier),
    ) {
        TableField(
            TableFieldModel.HeaderEdit(model.questionText),
            onFieldTextChanged = onHeaderTextChanged,
            onCloseClick = onCloseClick,
        )
        Spacer(modifier = Modifier.height(32.dp))

        TableField(
            TableFieldModel.SelectQuestionType(
                when (model) {
                    is QuestionEditModel.MultipleChoice -> FieldTypes.MULTIPLE
                    is QuestionEditModel.SingleChoice -> FieldTypes.SINGLE
                }
            ),
            updateFieldType = updateFieldType,
        )
        Spacer(modifier = Modifier.height(16.dp))
        model.answers.mapIndexed { position, item ->
            TableField(
                model = item,
                onSingleCheckboxSelect = { onSingleCheckboxSelect(position) },
                onMultipleCheckboxSelect = { onMultipleCheckboxSelect(position, it) },
                onFieldTextChanged = { onFieldTextChanged(position, it) },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (model.isAddAnswerButtonVisible) {
            TableField(
                model = TableFieldModel.NewItem,
                onNewItemClick = onNewItemClick,
            )
        }
    }
}
