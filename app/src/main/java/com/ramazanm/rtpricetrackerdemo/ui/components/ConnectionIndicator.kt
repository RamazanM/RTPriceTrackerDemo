package com.ramazanm.rtpricetrackerdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun ConnectionIndicator(modifier: Modifier= Modifier, isConnected: Boolean){
    return Box(
        modifier = modifier
            .padding(start = 16.dp)
            .size(24.dp)
            .clip(CircleShape)
            .background(if (isConnected) Color.Green else Color.Red)
            .testTag("ConnectionIndicator")
            .semantics {
                contentDescription =
                    if (isConnected) "Server is active" else "Server deactivated"
            }
    )
}