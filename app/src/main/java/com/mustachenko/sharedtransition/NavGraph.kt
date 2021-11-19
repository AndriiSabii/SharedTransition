package com.mustachenko.sharedtransition

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mustachenko.sharedtransition.screen.MyProductListScreen
import com.mustachenko.sharedtransition.screen.ProductInfoScreen
import com.mustachenko.sharedelement.sharedElementsComposable


@Composable
fun NavGraph() {
    val navController = rememberNavController()

    val startDestination = NavGraphNode.MyProducts

    NavHost(
        navController = navController,
        startDestination = startDestination.routeId
    ) {
        sharedElementsComposable(
            modifier = Modifier,
            startDestination = startDestination.routeId
        ) { controller ->
            with(controller) {
                with(NavGraphNode.MyProducts) {
                    composable(routeId) {
                        MyProductListScreen() { node ->
                            navigateTo(node)
                        }
                    }
                }

                with(NavGraphNode.ProductDetails) {
                    composable(
                        routeId,
                        arguments
                    ) { entry ->
                        val productId = entry.getArg<String>(key)
                        ProductInfoScreen(
                            productId
                        )
                    }
                }
            }
        }
    }
}

inline fun <reified T> NavBackStackEntry.getArg(key: String): T =
    arguments?.get(key) as T ?: throw NullPointerException("can not find value by key: $key")