package ru.lipt.coreui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.lipt.coreui.theme.MindTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PinField(
    modifier: Modifier = Modifier,
    title: String? = null,
    onTextChanged: (String) -> Unit = {}
) {
    val (item1, item2, item3, item4) = FocusRequester.createRefs()
    val itemsList = listOf(item1, item2, item3, item4)
    val textList = remember { MutableList(itemsList.size) { "" } }
    Column {
        title?.let {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                style = MindTheme.typography.material.body1,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyRow(
            modifier = Modifier.then(modifier),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            itemsIndexed(itemsList) { index, item ->
                val previousItem = if (index == 0) itemsList.first() else itemsList[index - 1]
                val nextItem = if (index == itemsList.lastIndex) itemsList.last() else itemsList[index + 1]
                OtpChar(
                    modifier = Modifier
                        .focusRequester(item)
                        .focusProperties {
                            next = nextItem
                            previous = previousItem
                        },
                    onTextChanged = { symbol ->
                        textList[index] = symbol
                        onTextChanged(textList.joinToString(separator = ""))
                    }
                )
            }
        }
    }
}

@Composable
private fun OtpChar(
    modifier: Modifier = Modifier,
    onTextChanged: (String) -> Unit,
) {
    var (text, setText) = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(
        key1 = text,
    ) {
        if (text.isNotEmpty()) {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Next,
            )
        }
    }

    OutlinedTextField(
        value = text,
        onValueChange = {
            if (it.length <= 1 &&
                ((it.isEmpty() || it.toCharArray().first().isDigit()))
            ) {
                setText(it)
                onTextChanged(it)
            }
        },
        modifier = modifier
            .width(50.dp)
            .onKeyEvent {
                if (it.key == Key.Tab) {
                    focusManager.moveFocus(FocusDirection.Next)
                    true
                }
                if (text.isEmpty() && it.key == Key.Backspace) {
                    focusManager.moveFocus(FocusDirection.Previous)
                }
                false
            },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
    )
}
