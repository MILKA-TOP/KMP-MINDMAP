package ru.lipt.testing.edit.question.base.models

import androidx.compose.runtime.Immutable

@Immutable
data class TableComponentModel(
    val id: String,
    val fields: List<TableFieldModel> = listOf(),
) {
    companion object {
        val EMPTY_EDITABLE_TABLE = TableComponentModel(
            id = "",
            fields = listOf(
                TableFieldModel.Header("TmpTest"),
                TableFieldModel.NewItem,
            )
        )
    }
}
