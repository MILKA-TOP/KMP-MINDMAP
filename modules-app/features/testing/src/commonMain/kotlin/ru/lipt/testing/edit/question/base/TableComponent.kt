package ru.lipt.testing.edit.question.base

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import ru.lipt.testing.edit.question.base.models.FieldTypes
import ru.lipt.testing.edit.question.base.models.TableComponentModel

@Composable
fun TableComponent(
    model: TableComponentModel,
    state: LazyListState = rememberLazyListState(),
    onSingleCheckboxSelect: (Int) -> Unit = {},
    onMultipleCheckboxSelect: (Int, Boolean) -> Unit = { _, _ -> },
    onNewItemClick: () -> Unit = {},
    onFieldTextChanged: (Int, String) -> Unit = { _, _ -> },
    updateFieldType: (Int, FieldTypes) -> Unit = { _, _ -> },
) {
    LazyColumn(
        state = state,
    ) {
        itemsIndexed(model.fields) { position, item ->
            TableField(
                model = item,
                onSingleCheckboxSelect = { onSingleCheckboxSelect(position) },
                onMultipleCheckboxSelect = { onMultipleCheckboxSelect(position, it) },
                onNewItemClick = onNewItemClick,
                onFieldTextChanged = { onFieldTextChanged(position, it) },
                updateFieldType = { updateFieldType(position, it) },
            )
        }
    }
}
