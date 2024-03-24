package ru.lipt.testing.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.lipt.coreui.shapes.RoundedCornerShape12
import ru.lipt.coreui.theme.MindTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberRow(
    list: List<Any>,
    horizontalPagerState: PagerState,
    lazyRowState: LazyListState,
    onIndicatorPageClick: (Int) -> Unit,
    addQuestion: () -> Unit = {},
    enabledShowAddItem: Boolean = true,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().heightIn(max = 64.dp), state = lazyRowState
    ) {
        items(list.size) { index ->
            Card(
                modifier = Modifier.sizeIn(minWidth = 36.dp, minHeight = 36.dp).clip(RoundedCornerShape12)
                    .clickable(onClick = { onIndicatorPageClick(index) }),
                backgroundColor = if (index == horizontalPagerState.currentPage) MaterialTheme.colors.surface
                else MindTheme.colors.unmarkedNode
            ) {
                Text(
                    text = (index + 1).toString(), textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
        if (enabledShowAddItem) {
            item {
                Card(
                    modifier = Modifier.sizeIn(minWidth = 36.dp, minHeight = 36.dp).clip(RoundedCornerShape12)
                        .clickable(onClick = addQuestion),
                    backgroundColor = MindTheme.colors.unmarkedNode,
                ) {
                    Text(
                        text = "+",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
