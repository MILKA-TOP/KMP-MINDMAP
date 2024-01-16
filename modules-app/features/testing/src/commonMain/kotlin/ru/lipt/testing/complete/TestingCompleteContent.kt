package ru.lipt.testing.complete

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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import ru.lipt.core.compose.alert.ErrorAlertDialog
import ru.lipt.core.compose.error.ErrorScreen
import ru.lipt.core.compose.loading.CircularProgressIndicatorLoadingScreen
import ru.lipt.core.compose.onError
import ru.lipt.core.compose.onLoading
import ru.lipt.core.compose.onSuccess
import ru.lipt.testing.common.compose.circleLayout

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TestingCompleteContent(
    screenModel: TestingCompleteScreenModel
) {
    val navigator = LocalNavigator.currentOrThrow
    val uiState = screenModel.uiState.collectAsState().value
    val ui = uiState.model
    val lazyRowState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    ErrorAlertDialog(
        error = uiState.alertError,
        onDismissRequest = screenModel::handleErrorAlertClose,
    )

    screenModel.handleNavigation { target ->
        when (target) {
            NavigationTarget.Result -> Unit
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Testing") }, navigationIcon = {
            IconButton(onClick = navigator::pop) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack, contentDescription = ""
                )
            }
        })
    }) {
        ui.onLoading {
            CircularProgressIndicatorLoadingScreen()
        }

        ui.onError { ErrorScreen(onRefresh = screenModel::init) }

        ui.onSuccess {
            val ui = data
            Column(modifier = Modifier.fillMaxSize()) {

                val horizontalPagerState = rememberPagerState() { ui.questions.size }

                HorizontalPager(
                    modifier = Modifier.weight(1f), state = horizontalPagerState
                ) { position ->
                    QuestionCompleteComponent(
                        model = ui.questions[position],
                        onSingleCheckboxSelect = { screenModel.onSingleSelectChanged(position, it) },
                        onMultipleCheckboxSelect = { pos, bool -> screenModel.onMultipleSelectChanged(position, pos, bool) },
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
                            modifier = Modifier.background(Color.Black, shape = CircleShape).circleLayout().padding(8.dp)
                                .clickable(onClick = {
                                    coroutineScope.launch {
                                        horizontalPagerState.animateScrollToPage(index)
                                    }
                                })
                        )
                    }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = ui.isAnswerButtonEnabled && !ui.buttonInProgress,
                    onClick = screenModel::onSaveButtonClick
                ) {
                    if (ui.buttonInProgress) {
                        CircularProgressIndicator()
                    } else {
                        Text("Done")
                    }
                }
            }
        }
    }
}
