package com.ramazanm.rtpricetrackerdemo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ramazanm.rtpricetrackerdemo.data.model.StockIndicator
import com.ramazanm.rtpricetrackerdemo.data.model.StockUi
import kotlinx.coroutines.delay

@Composable
fun StockItem(modifier: Modifier = Modifier, item: StockUi, onClick: () -> Unit) {
    val defaultColor = MaterialTheme.colorScheme.surfaceVariant
    var targetColor by remember { mutableStateOf(defaultColor) }

    LaunchedEffect(item) {
        targetColor = when (item.indicator) {
            StockIndicator.UP -> Color(0xff005500)
            StockIndicator.DOWN -> Color(0xff550000)
            StockIndicator.NEUTRAL -> defaultColor
        }
        if (targetColor != defaultColor) {
            delay(1000)
            targetColor = defaultColor
        }
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 1000)
    )

    return Card(
        colors = CardDefaults.cardColors(containerColor = animatedColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onClick()
            }) {
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(item.name)
            Text("$${String.format("%.2f", item.price)}")

            when (item.indicator) {
                StockIndicator.UP -> Icon(Icons.Filled.KeyboardArrowUp, "Up")
                StockIndicator.DOWN -> Icon(Icons.Filled.KeyboardArrowDown, "Down")
                StockIndicator.NEUTRAL -> Icon(Icons.Default.MoreVert, "Neutral")
            }

        }
    }
}