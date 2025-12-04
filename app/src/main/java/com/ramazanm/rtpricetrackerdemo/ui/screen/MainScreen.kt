package com.ramazanm.rtpricetrackerdemo.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ramazanm.rtpricetrackerdemo.MainActivity
import com.ramazanm.rtpricetrackerdemo.presentation.StockViewModel
import com.ramazanm.rtpricetrackerdemo.ui.components.ConnectionIndicator
import com.ramazanm.rtpricetrackerdemo.ui.components.StartStopButton
import com.ramazanm.rtpricetrackerdemo.ui.components.StockItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: StockViewModel = hiltViewModel()) {
    val stockList by viewModel.stockUpdates.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    fun onToggleServer() {
        if (isConnected) viewModel.stopWsConnection() else viewModel.startWsConnection()
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { }, navigationIcon = {
                ConnectionIndicator(isConnected = isConnected)
            }, actions = {
                StartStopButton(
                    isConnected = isConnected,
                    onClick = { onToggleServer() })
            })
        }) { paddingValues ->
        LazyColumn(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .testTag("LazyColumn"),
            verticalArrangement = if (!isConnected) Arrangement.Center else Arrangement.Top
        ) {
            if (!isConnected) {
                item {
                    Text(
                        "Please connect to the server to start tracking.",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { onToggleServer() })
                    )
                }
            }
            items(stockList) { item ->
                StockItem(item = item, onClick = {
                    navController.navigate(MainActivity.Detail(item.name))
                })
            }
        }
    }
}