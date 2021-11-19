package com.mustachenko.sharedelement

import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


fun NavGraphBuilder.sharedElementsComposable(
    modifier: Modifier,
    startDestination: String,
    duration: Int = 400,
    content: NavGraphBuilder.(NavHostController) -> Unit
) {
    composable(startDestination) {
        val targetState = remember {
            mutableStateOf(SharedElementType.FROM)
        }
        val rootState = remember {
            SharedElementRootState()
        }

        rootState.targetState = targetState
        rootState.transition.value =
            updateTransition(targetState.value, label = "sharedElementTransition")

        with(rootState) {
            val startElementPositioned = startElementPositionedState.value
            val endElementPositioned = endElementPositionedState.value

            LaunchedEffect(startElementPositioned, endElementPositioned) {
                if (startElementPositioned && endElementPositioned) {
                    transitionState.value = TransitionState.TO.TRANSITION
                    targetState.value = SharedElementType.TO
                } else if (startElementPositionedState.value) {
                    transitionState.value = TransitionState.FROM.TRANSITION
                    targetState.value = SharedElementType.FROM
                }
            }
        }

        Box(modifier = modifier.onGloballyPositioned { layoutCoordinates ->
            rootState.setRootCoordinates(layoutCoordinates)
        }) {
            CompositionLocalProvider(LocalSharedElementsRootState provides rootState) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    content(navController)
                }
            }
            SharedElementTransitionsOverlay(rootState, duration = duration)

            DisposableEffect(Unit) {
                onDispose {
                    rootState.onDispose()
                }
            }
        }
    }
}