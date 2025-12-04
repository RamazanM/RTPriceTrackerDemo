package com.ramazanm.rtpricetrackerdemo.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun StartStopButton(modifier: Modifier = Modifier, isConnected: Boolean, onClick: () -> Unit) {
    return OutlinedButton(
        modifier = modifier
            .padding(end = 16.dp)
            .testTag("ToggleServerButton"),
        onClick = onClick
    ) {
        Text(if (isConnected) "Disconnect" else "Connect")
    }
}