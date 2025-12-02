package com.ramazanm.rtpricetrackerdemo.ui.theme.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ramazanm.rtpricetrackerdemo.MainActivity
import com.ramazanm.rtpricetrackerdemo.data.model.StockIndicator
import com.ramazanm.rtpricetrackerdemo.presentation.StockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: StockViewModel = hiltViewModel()) {
    val stockList by viewModel.stockUpdates.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) Color.Green else Color.Red)
                            .testTag("ConnectionIndicator")
                            .semantics {
                                contentDescription =
                                    if (isConnected) "Server is active" else "Server deactivated"
                            }
                    )
                },
                actions = {
                    Button(
                        modifier = Modifier.testTag("ToggleServerButton"),
                        onClick = { if (isConnected) viewModel.stopWsConnection() else viewModel.startWsConnection() }) {
                        Text(if (isConnected) "Disconnect" else "Connect")
                    }
                }

            )
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .testTag("LazyColumn"),
        ) {
            items(stockList) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            navController.navigate(MainActivity.Detail(item.name))
                        }
                ) {
                    Row(
                        Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
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
        }
    }
}