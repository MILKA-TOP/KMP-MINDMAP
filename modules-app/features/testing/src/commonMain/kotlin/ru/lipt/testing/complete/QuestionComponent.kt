package ru.lipt.testing.complete

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.lipt.testing.complete.models.QuestionUiModel
import ru.lipt.testing.edit.question.base.TableField
import ru.lipt.testing.edit.question.base.models.TableFieldModel

@Composable
fun QuestionCompleteComponent(
    model: QuestionUiModel,
    state: LazyListState = rememberLazyListState(),
    onSingleCheckboxSelect: (Int) -> Unit = {},
    onMultipleCheckboxSelect: (Int, Boolean) -> Unit = { _, _ -> },
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = state,
    ) {
        item {
            TableField(
                TableFieldModel.Header(model.questionText),
            )
        }

        itemsIndexed(model.answers) { position, item ->
            TableField(
                model = item,
                onSingleCheckboxSelect = { onSingleCheckboxSelect(position) },
                onMultipleCheckboxSelect = { onMultipleCheckboxSelect(position, it) },
            )
        }
    }
}
