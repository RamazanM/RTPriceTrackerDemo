package com.ramazanm.rtpricetrackerdemo.domain

import com.ramazanm.rtpricetrackerdemo.data.model.StockIndicator
import com.ramazanm.rtpricetrackerdemo.data.model.StockUi
import com.ramazanm.rtpricetrackerdemo.data.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockCollectorUseCase @Inject constructor(val repository: StockRepository) {
    private val _stockListFlow = MutableStateFlow<List<StockUi>>(listOf())
    val stockListFlow = _stockListFlow.asStateFlow()

    suspend fun startCollecting() {
        repository.connect()
        repository.stockUpdates.collect { stockUpdate ->
            _stockListFlow.update { currentList ->
                val mutableList = currentList.toMutableList()
                val index = mutableList.indexOfFirst { it.name == stockUpdate.name }
                if(stockUpdate.name.isNotEmpty()) {
                    if (index != -1) {
                        val indicator = when {
                            stockUpdate.price > mutableList[index].price -> StockIndicator.UP
                            stockUpdate.price < mutableList[index].price -> StockIndicator.DOWN
                            else -> mutableList[index].indicator
                        }
                        mutableList[index] =
                            StockUi(
                                stockUpdate.name,
                                stockUpdate.price,
                                Date().time,
                                indicator,
                                stockUpdate.description
                            )
                    } else {
                        mutableList.add(
                            StockUi(
                                stockUpdate.name, stockUpdate.price, Date().time,
                                StockIndicator.NEUTRAL, stockUpdate.description
                            )
                        )
                    }
                }
                mutableList.sortedByDescending { it.price }
            }
        }
    }

    suspend fun stopCollecting() {
        repository.disconnect()
        _stockListFlow.value=listOf()
        println(_stockListFlow.value)
    }
}