package com.mustachenko.sharedtransition.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mustachenko.sharedelement.SharedElement
import com.mustachenko.sharedelement.SharedElementType

@Composable
fun ProductInfoScreen(
    productId: String
) {
    Row {
        SharedElement(
            tag = productId,
            modifier = Modifier.padding(start = 30.dp, top = 200.dp),
            type = SharedElementType.TO
        ) {
            Surface(color = Color.Blue, modifier = Modifier.size(200.dp)) {
                //empty
            }
        }
    }
}
