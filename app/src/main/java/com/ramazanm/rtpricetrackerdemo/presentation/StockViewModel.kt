package com.ramazanm.rtpricetrackerdemo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramazanm.rtpricetrackerdemo.data.model.StockIndicator
import com.ramazanm.rtpricetrackerdemo.data.model.StockUi
import com.ramazanm.rtpricetrackerdemo.data.repository.StockRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class StockViewModel(
    private val repository: StockRepository,
    dispatcher: CoroutineDispatcher? = null
) : ViewModel() {

    val coroutineScope = dispatcher?.let { CoroutineScope(it) } ?: viewModelScope

    private val _isConnected = MutableStateFlow(false)

    val isConnected = _isConnected.asStateFlow()

    private val _stockUpdates = MutableStateFlow<List<StockUi>>(listOf())

    val stockUpdates = _stockUpdates.asStateFlow()

    fun startWsConnection() {
        coroutineScope.launch {
            repository.connect()
            _isConnected.emit(true)
            repository.stockUpdates.collect { stockUpdate ->
                val currentList = _stockUpdates.value.toMutableList()
                val index = currentList.indexOfFirst { it.name == stockUpdate.name }
                if (index != -1) {
                    val indicator = when {
                        stockUpdate.price > currentList[index].price -> StockIndicator.UP
                        stockUpdate.price < currentList[index].price -> StockIndicator.DOWN
                        else -> StockIndicator.NEUTRAL
                    }
                    currentList[index] =
                        StockUi(stockUpdate.name, stockUpdate.price, Date().time, indicator)
                } else {
                    currentList.add(
                        StockUi(
                            stockUpdate.name, stockUpdate.price, Date().time,
                            StockIndicator.NEUTRAL
                        )
                    )
                }

                _stockUpdates.update { currentList.sortedByDescending { it.price } }
            }
        }
    }


    fun stopWsConnection() {
        repository.disconnect()
        coroutineScope.launch {
            _isConnected.emit(false)
            _stockUpdates.emit(listOf())
        }

    }
}