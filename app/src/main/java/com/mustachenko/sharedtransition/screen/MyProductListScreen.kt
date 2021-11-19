package com.mustachenko.sharedtransition.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mustachenko.sharedelement.SharedElement
import com.mustachenko.sharedelement.SharedElementType
import com.mustachenko.sharedtransition.NavGraphNode

@Composable
fun MyProductListScreen(
    navigateTo: (NavGraphNode) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp)
    ) {
        val onItemClick: (id: String) -> Unit = {
            navigateTo(NavGraphNode.ProductDetails(id = it))
        }

        items(20) { index ->
            SharedElement(tag = index.toString(), type = SharedElementType.FROM) {
                ProductListItem(index) {
                    onItemClick(it.toString())
                }
            }
        }
    }
}

@Composable
fun ProductListItem(position: Int, onClick: (id: Int) -> Unit) {
    Box(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp)) {
        val shape = RoundedCornerShape(6.dp)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp,
            shape = shape,
        ) {
            Row(
                modifier = Modifier
                    .clip(shape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            onClick(position)
                        },
                        indication = rememberRipple(bounded = true)
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = position.toString(),
                    Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}