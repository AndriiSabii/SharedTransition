package com.mustachenko.sharedelement

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot

internal val LocalSharedElementsRootState = compositionLocalOf<SharedElementRootState> {
    error("SharedElementsRoot not found. SharedElement must be hosted in SharedElementsRoot.")
}

internal sealed class TransitionState(open val weight: Int) {
    object NONE : TransitionState(0)
    sealed class FROM(override val weight: Int) : TransitionState(weight) {
        object TRANSITION : FROM(1)
        object AFTER : FROM(2)
    }

    sealed class TO(override val weight: Int) : TransitionState(weight) {
        object TRANSITION : TO(1)
        object AFTER : TO(2)
    }
}

internal data class SharedElementInfo(val tag: SharedElementTag, val type: SharedElementType)

internal class PositionedSharedElement(
    val info: SharedElementInfo,
    val placeholder: @Composable() () -> Unit,
    val bounds: Rect
)

internal class SharedElementRootState {
    val startElementPositionedState: MutableState<Boolean> = mutableStateOf(false)
    val endElementPositionedState: MutableState<Boolean> = mutableStateOf(false)
    val transitionState: MutableState<TransitionState> = mutableStateOf(TransitionState.NONE)

    var targetState: MutableState<SharedElementType>? = null
    var transition: MutableState<Transition<SharedElementType>?> = mutableStateOf(null)

    private var rootCoordinates: LayoutCoordinates? = null

    private val fromTrackers = mutableMapOf<SharedElementTag, PositionedSharedElement>()
    private val toTrackers = mutableMapOf<SharedElementTag, PositionedSharedElement>()

    @OptIn(ExperimentalStdlibApi::class)
    val tags: List<SharedElementTag>
        get() {
            val keys = buildList {
                addAll(toTrackers.keys)
                addAll(fromTrackers.keys)
            }
            return keys.groupBy { it }.filter { it.value.size == 2 }.map { it.key }
        }

    fun setRootCoordinates(rootCoordinates: LayoutCoordinates) {
        this.rootCoordinates = rootCoordinates
    }

    fun getTracker(tag: SharedElementTag, type: SharedElementType) =
        when (type) {
            SharedElementType.TO -> {
                toTrackers[tag]
            }
            SharedElementType.FROM -> {
                fromTrackers[tag]
            }
        }

    private fun clearToTrackers() {
        toTrackers.clear()
    }

    fun onTransitionEnd() {
        val targetValue = transitionState.value
        if (targetValue == TransitionState.FROM.TRANSITION) {
            transitionState.value = TransitionState.FROM.AFTER
            clearToTrackers()
        } else if (targetValue == TransitionState.TO.TRANSITION) {
            transitionState.value = TransitionState.TO.AFTER
        }
    }

    fun shouldHideElement(elementInfo: SharedElementInfo): Boolean {
        return when (elementInfo.type) {
            SharedElementType.FROM -> {
                when (val value = transitionState.value) {
                    is TransitionState.FROM -> value.weight < TransitionState.FROM.AFTER.weight
                    is TransitionState.TO -> true
                    else -> false
                }
            }
            SharedElementType.TO -> {
                when (val value = transitionState.value) {
                    is TransitionState.TO -> value.weight < TransitionState.TO.TRANSITION.weight
                    is TransitionState.FROM -> true
                    else -> false
                }
            }
        } || transition.value?.isRunning ?: false && isContainsInTrackers(elementInfo.tag)
    }

    fun onElementPositioned(
        elementInfo: SharedElementInfo,
        placeholder: @Composable () -> Unit,
        coordinates: LayoutCoordinates
    ) {
        when (elementInfo.type) {
            SharedElementType.FROM -> {
                startElementPositionedState
            }
            SharedElementType.TO -> {
                endElementPositionedState
            }
        }.value = true

        val element = PositionedSharedElement(
            info = elementInfo,
            placeholder = placeholder,
            bounds = coordinates.boundsInRoot()
        )

        when (elementInfo.type) {
            SharedElementType.FROM -> {
                fromTrackers[element.info.tag] = element
            }
            SharedElementType.TO -> {
                toTrackers[element.info.tag] = element
            }
        }
    }

    fun onElementDisposed(elementInfo: SharedElementInfo) {
        when (elementInfo.type) {
            SharedElementType.FROM -> {
                startElementPositionedState
            }
            SharedElementType.TO -> {
                endElementPositionedState
            }
        }.value = false
    }

    fun onDispose() {
        toTrackers.clear()
        fromTrackers.clear()
    }

    private fun isContainsInTrackers(tag: SharedElementTag) = fromTrackers.contains(tag) &&
            toTrackers.containsKey(tag)
}