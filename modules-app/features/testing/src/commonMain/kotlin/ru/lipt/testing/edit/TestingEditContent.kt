package ru.lipt.testing.edit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import ru.lipt.testing.common.compose.circleLayout
import ru.lipt.testing.edit.question.QuestionEditComponent

@OptIn(ExperimentalFoundationApi::class)
@Suppress("UnusedPrivateMember")
@Composable
fun TestingEditContent(
    screenModel: TestingEditScreenModel,
) {
    val navigator = LocalNavigator.currentOrThrow

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Testing") }, navigationIcon = {
            IconButton(onClick = navigator::pop) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack, contentDescription = ""
                )
            }
        })
    }) {
        val uiState = screenModel.uiState.collectAsState().value
        val ui = uiState.model
        val horizontalPagerState = rememberPagerState() { ui.questions.size }
        val lazyRowState = rememberLazyListState()
        val snackBarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        screenModel.handleNavigation { target ->
            when (target) {
                NavigationTarget.SuccessQuestionsSave -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Saved")
                    }
                }
                NavigationTarget.IncorrectQuestions -> {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("incorrect")
                    }
                }
                is NavigationTarget.OpenQuestions -> {
                    coroutineScope.launch {
                        horizontalPagerState.animateScrollToPage(target.position)
                    }
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {

            HorizontalPager(
                modifier = Modifier.weight(1f), state = horizontalPagerState
            ) { position ->
                QuestionEditComponent(
                    model = ui.questions[position],
                    onHeaderTextChanged = { screenModel.onHeaderTextChanged(position, it) },
                    onSingleCheckboxSelect = { screenModel.onSingleSelectChanged(position, it) },
                    onMultipleCheckboxSelect = { pos, bool -> screenModel.onMultipleSelectChanged(position, pos, bool) },
                    onNewItemClick = { screenModel.onNewItemAdd(position) },
                    onFieldTextChanged = { pos, text -> screenModel.onItemTextChanged(position, pos, text) },
                )
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth().heightIn(max = 64.dp), state = lazyRowState
            ) {
                itemsIndexed(ui.questions) { index, item ->
                    Text(
                        text = "${index + 1}",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier.background(Color.Black, shape = CircleShape).circleLayout().padding(8.dp).clickable(onClick = {
                            screenModel.onIndicatorPageClick(index)
                        })
                    )
                }
                item {
                    Text(
                        text = "+",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier.background(Color.Black, shape = CircleShape).circleLayout().padding(8.dp)
                            .clickable(onClick = screenModel::addQuestion)
                    )
                }
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = screenModel::onGenerateQuestionButtonCLick) {
                Text("Generate questions")
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = screenModel::onSaveButtonClick) {
                Text("Save")
            }
        }
        SnackbarHost(hostState = snackBarHostState)
    }
}
