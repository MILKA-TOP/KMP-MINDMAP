package ru.lipt.login.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import ru.lipt.coreui.theme.MindTheme
import ru.lipt.login.MR
import ru.lipt.login.common.navigation.LoginNavigationDestinations

@Composable
fun SplashContent(
    screen: Screen,
    screenModel: SplashScreenModel = screen.getScreenModel()
) {

    val uiState = screenModel.uiState.collectAsState().value
    val navigator = LocalNavigator.currentOrThrow

    screenModel.handleNavigation { target ->
        when (target) {
            NavigationTarget.HelloScreenNavigate -> {
                navigator.replaceAll(ScreenRegistry.get(LoginNavigationDestinations.HelloScreenDestination))
            }
            NavigationTarget.PinInputScreenNavigate -> {
                navigator.replaceAll(ScreenRegistry.get(LoginNavigationDestinations.PinInputScreenDestination))
            }
        }
    }

    Content()
}

@Composable
private fun Content() {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface)) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painterResource(MR.images.MindIcon),
                modifier = Modifier.size(200.dp).clip(CircleShape),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                stringResource(MR.strings.splash_screen_text),
                style = MindTheme.typography.material.h5,
                textAlign = TextAlign.Center,
            )
        }
    }
}
