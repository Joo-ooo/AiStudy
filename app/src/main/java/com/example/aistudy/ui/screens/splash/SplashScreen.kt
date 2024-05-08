package com.example.aistudy.ui.screens.splash

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.aistudy.R

    /**
     * Defines a composable function for a SplashScreen that animates an image logo when the app is launched.
     * This screen serves as an introductory visual for users before transitioning to the main content of the app.
     *
     * Key Components:
     * - `startAnimation`: A state to control the start of the animation. It initiates as false and is set to true
     *   once the LaunchedEffect is executed.
     * - `offsetState` and `alphaState`: Animated states for moving the image vertically and fading it in, respectively.
     *   `offsetState` animates the Y offset of the image to create a sliding effect, while `alphaState` controls the
     *   opacity of the image for a fade-in effect.
     * - `LaunchedEffect`: Used to start the animation automatically upon the screen's initial composition and
     *   navigate to the main content screen after a delay.
     *
     * Functionality:
     * - Upon composition, the SplashScreen triggers an animation that simultaneously fades in the app's logo and
     *   moves it to a central position.
     * - After the animation completes and a brief pause (2000 milliseconds), the screen automatically navigates
     *   to the application's main list screen.
     * - The animation and subsequent screen transition provide a smooth and visually appealing introduction to the app.
     *
     * Usage:
     * - The SplashScreen is typically used as the first visual element displayed when the app is opened, preceding
     *   the main content or navigation structure of the application.
     */

@Composable
fun SplashScreen(navigateToListScreen: () -> Unit) {

    var startAnimation by remember {
        mutableStateOf(false)
    }

    val offsetState by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 200.dp,
        animationSpec = tween(1000)
    )

    val alphaState by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000)
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000)
        navigateToListScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .size(200.dp)
                .offset(y = offsetState)
                .alpha(alphaState),
            painter = painterResource(id = R.drawable.img),
            contentDescription = stringResource(id = R.string.app_logo)
        )
    }
}

@Composable
@Preview
fun SplashScreenPreview() {
    SplashScreen(navigateToListScreen = {})
}