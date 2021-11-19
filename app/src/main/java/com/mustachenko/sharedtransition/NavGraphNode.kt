package com.mustachenko.sharedtransition

import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class NavGraphNode(val routeId: String) {

    object MyProducts : NavGraphNode("myProducts") {
        fun NavHostController.navigateTo(
            node: NavGraphNode
        ) {
            navigate(node.routeId) {
                launchSingleTop = true
            }
        }
    }

    class ProductDetails(id: String) : NavGraphNode("$route$id") {

        companion object {
            const val key: String = "productId"
            private const val route = "ProductDetails/"

            const val routeId = "$route{$key}"
            val arguments = listOf(navArgument(key) {
                type = NavType.StringType
            })
        }
    }

}
