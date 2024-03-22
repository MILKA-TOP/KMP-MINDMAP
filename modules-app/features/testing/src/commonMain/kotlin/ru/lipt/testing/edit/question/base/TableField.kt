package ru.lipt.testing.edit.question.base

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import ru.lipt.core.compose.OutlinedCountedTextField
import ru.lipt.coreui.shapes.RoundedCornerShape16
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.testing.edit.question.base.models.AnswerResultType
import ru.lipt.testing.edit.question.base.models.FieldTypes
import ru.lipt.testing.edit.question.base.models.TableFieldModel

@Composable
fun TableField(
    model: TableFieldModel,
    onSingleCheckboxSelect: () -> Unit = {},
    onMultipleCheckboxSelect: (Boolean) -> Unit = {},
    onNewItemClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onFieldTextChanged: (String) -> Unit = {},
    updateFieldType: (FieldTypes) -> Unit = {},
) {
    var popUpState by remember { mutableStateOf(false) }
    var onPopUpOpen: () -> Unit = { popUpState = true }

    when (model) {
        is TableFieldModel.Header -> Header(model)
        is TableFieldModel.Caption -> Caption(model)
        is TableFieldModel.HeaderEdit -> HeaderEdit(model, onFieldTextChanged, onCloseClick)
        is TableFieldModel.SelectQuestionType -> SelectQuestionType(model, onPopUpOpen)
        is TableFieldModel.SingleCheckboxSelect -> SingleCheckboxSelect(model, onSingleCheckboxSelect)
        is TableFieldModel.MultipleCheckboxSelect -> MultipleCheckboxSelect(model, onMultipleCheckboxSelect)
        is TableFieldModel.SingleCheckboxEdit -> SingleCheckboxEdit(model, onSingleCheckboxSelect, onFieldTextChanged)
        is TableFieldModel.MultipleCheckboxEdit -> MultipleCheckboxEdit(model, onMultipleCheckboxSelect, onFieldTextChanged)

        is TableFieldModel.NewItem -> NewItem(onNewItemClick)
    }

    if (popUpState) {
        Popup(alignment = Alignment.Center, onDismissRequest = { popUpState = false }) {
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
    onCloseClick: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedCountedTextField(
            maxSymbols = 200,
            label = {
                Text("Question")
            }, value = model.text, onValueChange = onValueChange
        )
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = onCloseClick) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun SingleCheckboxSelect(
    model: TableFieldModel.SingleCheckboxSelect,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().border(
            width = 4.dp, color = when (model.resultType) {
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
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        RadioButton(
            selected = model.isSelected,
            onClick = onClick,
        )
        OutlinedTextField(modifier = Modifier.weight(1f), value = model.text, onValueChange = onValueChange, singleLine = true)
    }
}

@Composable
private fun MultipleCheckboxSelect(
    model: TableFieldModel.MultipleCheckboxSelect,
    onClick: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().border(
            width = 4.dp, color = when (model.resultType) {
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
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Checkbox(
            checked = model.isSelected,
            onCheckedChange = onClick,
        )
        OutlinedTextField(modifier = Modifier.weight(1f), value = model.text, onValueChange = onValueChange, singleLine = true)
    }
}

@Composable
private fun NewItem(onItemClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(), onClick = onItemClick, colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = MindTheme.colors.unmarkedNode, contentColor = MindTheme.colors.material.onBackground
        )
    ) {
        Text("Add new Item")
    }
}

@Composable
private fun SelectQuestionType(
    model: TableFieldModel.SelectQuestionType,
    onUpdateFieldTypeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape16).clickable(onClick = onUpdateFieldTypeClick)
    ) {
        Column(
            modifier = Modifier.padding(all = 4.dp).padding(start = 16.dp)
        ) {
            Text(text = "Selected question type: ", style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = model.type.name, style = MaterialTheme.typography.body1)
        }
    }
}
