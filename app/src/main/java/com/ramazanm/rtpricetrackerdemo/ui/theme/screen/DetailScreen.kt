package com.ramazanm.rtpricetrackerdemo.ui.theme.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ramazanm.rtpricetrackerdemo.data.model.StockIndicator
import com.ramazanm.rtpricetrackerdemo.presentation.StockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, viewModel: StockViewModel, name: String) {
    val stocksData = viewModel.stockUpdates.collectAsState()
    val selectedStockData by derivedStateOf { stocksData.value.find { it.name == name }?: stocksData.value.first() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedStockData.name,
                        modifier = Modifier.testTag("SymbolDetailsTitle")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = selectedStockData.name)
            Text(
                text = "$${String.format("%.2f", selectedStockData.price)}",
                color = when(selectedStockData.indicator){
                    StockIndicator.UP -> Color.Green
                    StockIndicator.DOWN -> Color.Red
                    StockIndicator.NEUTRAL -> Color.Black
                }
            )
            Text(
                text = selectedStockData.details,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}