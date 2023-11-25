package ru.lipt.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
@Suppress("UnusedPrivateMember")
fun DetailsContent(
    screenModel: DetailsScreenModel,
) {
    val navigator = LocalNavigator.currentOrThrow

    Box(modifier = Modifier.fillMaxSize()) {
        Column {

            Text("Details Screen")
            Button(onClick = navigator::pop) {
                Text("Back")
            }
        }
    }
}
