package ru.lipt.testing.edit.question

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.lipt.testing.edit.question.base.TableField
import ru.lipt.testing.edit.question.base.models.FieldTypes
import ru.lipt.testing.edit.question.base.models.TableFieldModel

@Composable
fun QuestionComponent(
    model: QuestionEditModel,
    state: LazyListState = rememberLazyListState(),
    onSingleCheckboxSelect: (Int) -> Unit = {},
    onMultipleCheckboxSelect: (Int, Boolean) -> Unit = { _, _ -> },
    onNewItemClick: () -> Unit = {},
    onHeaderTextChanged: (String) -> Unit = { _ -> },
    onFieldTextChanged: (Int, String) -> Unit = { _, _ -> },
    updateFieldType: (Int, FieldTypes) -> Unit = { _, _ -> },
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = state,
    ) {
        item {
            TableField(
                TableFieldModel.HeaderEdit(model.questionText),
                onFieldTextChanged = onHeaderTextChanged,
            )
        }

        itemsIndexed(model.answers) { position, item ->
            TableField(
                model = item,
                onSingleCheckboxSelect = { onSingleCheckboxSelect(position) },
                onMultipleCheckboxSelect = { onMultipleCheckboxSelect(position, it) },
                onFieldTextChanged = { onFieldTextChanged(position, it) },
                updateFieldType = { updateFieldType(position, it) },
            )
        }
        if (model.isAddAnswerButtonVisible) {
            item {
                TableField(
                    model = TableFieldModel.NewItem,
                    onNewItemClick = onNewItemClick,
                )
            }
        }
    }
}
