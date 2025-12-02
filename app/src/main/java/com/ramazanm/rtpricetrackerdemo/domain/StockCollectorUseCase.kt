package com.ramazanm.rtpricetrackerdemo.domain

import com.ramazanm.rtpricetrackerdemo.data.model.StockIndicator
import com.ramazanm.rtpricetrackerdemo.data.model.StockUi
import com.ramazanm.rtpricetrackerdemo.data.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import javax.inject.Inject

class StockCollectorUseCase @Inject constructor(val repository: StockRepository) {
    private val _stockListFlow = MutableStateFlow<List<StockUi>>(listOf())
    val stockListFlow = _stockListFlow.asStateFlow()

    suspend fun startCollecting() {
        repository.connect()
        repository.stockUpdates.collect { stockUpdate ->
            val currentList = _stockListFlow.value.toMutableList()
            val index = currentList.indexOfFirst { it.name == stockUpdate.name }
            if (index != -1 && stockUpdate.name.isNotEmpty()) {
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

            _stockListFlow.update { currentList.sortedByDescending { it.price } }


        }
    }
    suspend fun  stopCollecting(){
        repository.disconnect()
        _stockListFlow.update { listOf() }
    }
}