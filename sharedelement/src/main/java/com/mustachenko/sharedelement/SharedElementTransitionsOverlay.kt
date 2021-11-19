package com.mustachenko.sharedelement

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset


@Composable
internal fun SharedElementTransitionsOverlay(rootState: SharedElementRootState, duration: Int) {
    val transition = rootState.transition.value ?: return
    val targetState = rootState.targetState?.value ?: return

    if (transition.currentState != targetState) {
        rootState.tags.forEach { tag ->
            val offset by transition.animateIntOffset(
                label = "offset",
                transitionSpec = {
                    tween(duration)
                }
            ) { targetState ->
                val elementInfo = rootState.getTracker(tag, targetState)
                val bounds = elementInfo?.bounds
                if (bounds != null) {
                    IntOffset(bounds.left.toInt(), bounds.top.toInt())
                } else IntOffset.Zero
            }

            val alpha by transition.animateFloat(
                label = "alpha",
                transitionSpec = {
                    tween(duration)
                }
            ) { targetState ->
                when (targetState) {
                    SharedElementType.TO -> 1f
                    SharedElementType.FROM -> 0f
                }
            }

            val scaleXFrom by transition.animateFloat(
                label = "scaleXFrom",
                transitionSpec = {
                    tween(duration)
                }
            ) { targetState ->
                val fromElement = rootState.getTracker(tag, SharedElementType.FROM)
                val toElement = rootState.getTracker(tag, SharedElementType.TO)
                if (fromElement != null && toElement != null) {
                    when (targetState) {
                        SharedElementType.TO -> {
                            toElement.bounds.width / fromElement.bounds.width
                        }
                        SharedElementType.FROM -> {
                            1f
                        }
                    }
                } else 0f
            }

            val scaleYFrom by transition.animateFloat(
                label = "scaleYFrom",
                transitionSpec = {
                    tween(duration)
                }
            ) { targetState ->
                val fromElement = rootState.getTracker(tag, SharedElementType.FROM)
                val toElement = rootState.getTracker(tag, SharedElementType.TO)
                if (fromElement != null && toElement != null) {
                    when (targetState) {
                        SharedElementType.TO -> {
                            toElement.bounds.height / fromElement.bounds.height
                        }
                        SharedElementType.FROM -> {
                            1f
                        }
                    }
                } else 0f
            }

            val scaleXTo by transition.animateFloat(
                label = "scaleXTo",
                transitionSpec = {
                    tween(duration)
                }
            ) { targetState ->
                val fromElement = rootState.getTracker(tag, SharedElementType.FROM)
                val toElement = rootState.getTracker(tag, SharedElementType.TO)
                if (fromElement != null && toElement != null) {
                    when (targetState) {
                        SharedElementType.TO -> {
                            1f
                        }
                        SharedElementType.FROM -> {
                            fromElement.bounds.width / toElement.bounds.width
                        }
                    }
                } else 0f
            }

            val scaleYTo by transition.animateFloat(
                label = "scaleYTo",
                transitionSpec = {
                    tween(duration)
                }
            ) { targetState ->
                val fromElement = rootState.getTracker(tag, SharedElementType.FROM)
                val toElement = rootState.getTracker(tag, SharedElementType.TO)
                if (fromElement != null && toElement != null) {
                    when (targetState) {
                        SharedElementType.TO -> {
                            1f
                        }
                        SharedElementType.FROM -> {
                            fromElement.bounds.height / toElement.bounds.height
                        }
                    }
                } else 0f
            }

            val innerOffset by transition.animateIntOffset(
                label = "innerOffset",
                transitionSpec = {
                    tween(duration)
                }
            ) { targetState ->
                val fromElement = rootState.getTracker(tag, SharedElementType.FROM)
                val toElement = rootState.getTracker(tag, SharedElementType.TO)
                if (fromElement != null && toElement != null) {
                    val fromWidth = fromElement.bounds.width.toInt()
                    val toWidth = toElement.bounds.width.toInt()
                    val fromHeight = fromElement.bounds.height.toInt()
                    val toHeight = toElement.bounds.height.toInt()

                    when (targetState) {
                        SharedElementType.TO -> {
                            IntOffset(
                                x = if (fromWidth > toWidth) (toWidth - fromWidth) / 2 else 0,
                                y = if (fromHeight > toHeight) (toHeight - fromHeight) / 2 else 0
                            )
                        }
                        SharedElementType.FROM -> {
                            IntOffset(
                                x = if (fromWidth < toWidth) (fromWidth - toWidth) / 2 else 0,
                                y = if (fromHeight < toHeight) (fromHeight - toHeight) / 2 else 0
                            )
                        }
                    }
                } else IntOffset.Zero
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        offset
                    },
            ) {
                Box(
                    modifier = Modifier
                        .offset {
                            innerOffset
                        }
                ) {
                    Box(
                        Modifier
                            .align(Alignment.Center)
                            .scale(scaleXTo, scaleYTo)
                            .alpha(alpha)
                    ) {
                        rootState.getTracker(tag, SharedElementType.TO)?.placeholder?.invoke()
                    }
                    Box(
                        Modifier
                            .align(Alignment.Center)
                            .scale(scaleXFrom, scaleYFrom)
                            .alpha(1 - alpha)
                    ) {
                        rootState.getTracker(tag, SharedElementType.FROM)?.placeholder?.invoke()
                    }
                }
            }
        }
    } else {
        rootState.onTransitionEnd()
    }
}
