package ru.lipt.testing.edit.question.base

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import ru.lipt.testing.edit.question.base.models.AnswerResultType
import ru.lipt.testing.edit.question.base.models.FieldTypes
import ru.lipt.testing.edit.question.base.models.TableFieldModel

@Composable
fun TableField(
    model: TableFieldModel,
    onSingleCheckboxSelect: () -> Unit = {},
    onMultipleCheckboxSelect: (Boolean) -> Unit = {},
    onNewItemClick: () -> Unit = {},
    onFieldTextChanged: (String) -> Unit = {},
    updateFieldType: (FieldTypes) -> Unit = {},
) {
    var popUpState by remember { mutableStateOf(false) }
    var onPopUpOpen: () -> Unit = { popUpState = true }

    when (model) {
        is TableFieldModel.Header -> Header(model)
        is TableFieldModel.Caption -> Caption(model)
        is TableFieldModel.HeaderEdit -> HeaderEdit(model, onFieldTextChanged)
        is TableFieldModel.SingleCheckboxSelect -> SingleCheckboxSelect(model, onSingleCheckboxSelect)
        is TableFieldModel.MultipleCheckboxSelect -> MultipleCheckboxSelect(model, onMultipleCheckboxSelect)
        is TableFieldModel.SingleCheckboxEdit -> SingleCheckboxEdit(model, onSingleCheckboxSelect, onFieldTextChanged, onPopUpOpen)
        is TableFieldModel.MultipleCheckboxEdit -> MultipleCheckboxEdit(model, onMultipleCheckboxSelect, onFieldTextChanged, onPopUpOpen)

        is TableFieldModel.NewItem -> NewItem(onNewItemClick)
    }

    if (popUpState) {
        Popup(alignment = Alignment.Center,
            onDismissRequest = { popUpState = false }
        ) {
            Column {
                Button(onClick = {
                    popUpState = false
                    updateFieldType(FieldTypes.SINGLE)
                }) {
                    Text(text = "Single")
                }
                Button(onClick = {
                    popUpState = false
                    updateFieldType(FieldTypes.MULTIPLE)
                }) {
                    Text(text = "Mult")
                }
            }
        }
    }
}

@Composable
private fun Header(model: TableFieldModel.Header) {
    Text(modifier = Modifier.fillMaxWidth(), text = model.text)
}

@Composable
private fun Caption(model: TableFieldModel.Caption) {
    Text(modifier = Modifier.fillMaxWidth(), text = model.text)
}

@Composable
private fun HeaderEdit(
    model: TableFieldModel.HeaderEdit,
    onValueChange: (String) -> Unit,
) {
    TextField(modifier = Modifier.fillMaxWidth(), value = model.text, onValueChange = onValueChange)
}

@Composable
private fun SingleCheckboxSelect(
    model: TableFieldModel.SingleCheckboxSelect,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .border(
                width = 4.dp,
                color = when (model.resultType) {
                    AnswerResultType.NONE -> Color.Unspecified
                    AnswerResultType.ERROR -> Color.Red
                    AnswerResultType.CORRECT -> Color.Green
                }
            )
    ) {
        RadioButton(
            selected = model.isSelected,
            onClick = onClick,
            enabled = model.enabled,
        )
        Text(modifier = Modifier.weight(1f), text = model.text)
    }
}

@Composable
private fun SingleCheckboxEdit(
    model: TableFieldModel.SingleCheckboxEdit,
    onClick: () -> Unit,
    onValueChange: (String) -> Unit,
    onPopUpOpen: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        RadioButton(
            selected = model.isSelected,
            onClick = onClick,
        )
        TextField(modifier = Modifier.weight(1f), value = model.text, onValueChange = onValueChange)
        Button(onClick = onPopUpOpen) {
            Icon(Icons.Filled.MoreVert, contentDescription = null)
        }
    }
}

@Composable
private fun MultipleCheckboxSelect(
    model: TableFieldModel.MultipleCheckboxSelect,
    onClick: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 4.dp,
                color = when (model.resultType) {
                    AnswerResultType.NONE -> Color.Unspecified
                    AnswerResultType.ERROR -> Color.Red
                    AnswerResultType.CORRECT -> Color.Green
                }
            )
    ) {
        Checkbox(
            checked = model.isSelected,
            onCheckedChange = onClick,
            enabled = model.enabled,
        )
        Text(modifier = Modifier.weight(1f), text = model.text)
    }
}

@Composable
private fun MultipleCheckboxEdit(
    model: TableFieldModel.MultipleCheckboxEdit,
    onClick: (Boolean) -> Unit,
    onValueChange: (String) -> Unit,
    onPopUpOpen: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Checkbox(
            checked = model.isSelected,
            onCheckedChange = onClick,
        )
        TextField(modifier = Modifier.weight(1f), value = model.text, onValueChange = onValueChange)
        Button(onClick = onPopUpOpen) {
            Icon(Icons.Filled.MoreVert, contentDescription = null)
        }
    }
}

@Composable
private fun NewItem(onItemClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onItemClick
    ) {
        Text("Add new Item")
    }
}
