package com.mustachenko.sharedelement

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned

enum class SharedElementType { FROM, TO }

typealias SharedElementTag = String

@Composable
fun SharedElement(
    tag: String,
    type: SharedElementType,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val elementInfo = SharedElementInfo(tag, type)
    val rootState = LocalSharedElementsRootState.current
    val alpha = if (rootState.shouldHideElement(elementInfo)) 0f else 1f
    Box(modifier = modifier
        .alpha(alpha)
        .onGloballyPositioned { coordinates ->
            rootState.onElementPositioned(
                elementInfo = elementInfo,
                placeholder = placeholder ?: content,
                coordinates = coordinates,
            )
        }
    ) {
        content()
    }

    DisposableEffect(tag) {
        onDispose {
            rootState.onElementDisposed(elementInfo)
        }
    }
}