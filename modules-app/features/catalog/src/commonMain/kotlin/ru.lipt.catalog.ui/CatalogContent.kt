package ru.lipt.catalog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ru.lipt.catalog.ui.models.MapCatalogElement
import ru.lipt.navigation.MainNavigator
import ru.lipt.navigation.params.map.MapScreenParams

@Composable
fun CatalogContent(
    screenModel: CatalogScreenModel,
) {

    val navigator = LocalNavigator.currentOrThrow

    val ui = screenModel.uiState.collectAsState().value

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ui.maps.forEach { map ->
            val mapScreen = rememberScreen(MainNavigator.MapScreenDestination(params = MapScreenParams(map.id)))
            MapListElement(
                map = map,
                toMapNavigate = { id -> navigator.push(mapScreen) }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MapListElement(
    map: MapCatalogElement,
    toMapNavigate: (String) -> Unit // :TODO replace to call VM for navigation and navigate by navigationTarget
) {
    Column(
        modifier = Modifier
            .sizeIn(maxWidth = 400.dp, maxHeight = 300.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colors.background)
            .clickable(onClick = { toMapNavigate(map.id) })
    ) {
        Text(
            text = map.title,
            style = MaterialTheme.typography.h5,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = map.description,
            style = MaterialTheme.typography.body1,
        )
    }
}
