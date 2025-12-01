package com.ramazanm.rtpricetrackerdemo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramazanm.rtpricetrackerdemo.data.model.StockDTO
import com.ramazanm.rtpricetrackerdemo.data.repository.StockRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StockViewModel(
    private val repository: StockRepository,
    dispatcher: CoroutineDispatcher? = null
) : ViewModel() {

    val coroutineScope = dispatcher?.let { CoroutineScope(it) } ?: viewModelScope

    private val _isConnected = MutableStateFlow(false)

    val isConnected = _isConnected.asStateFlow()

    private val _stockUpdates = MutableStateFlow<List<StockDTO>>(listOf())

    val stockUpdates = _stockUpdates.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    private val errorMsg = _errorMsg.asStateFlow()

    fun startWsConnection() {
        coroutineScope.launch {
            repository.connect()
            _isConnected.emit(true)
            repository.stockUpdates.collect { stockUpdate ->
                if(!isConnected.value) return@collect
                if(stockUpdate.errorMsg!=null){
                    _errorMsg.emit(stockUpdate.errorMsg)
                    return@collect
                }
                val currentList = _stockUpdates.value.toMutableList()
                val index = currentList.indexOfFirst { it.name == stockUpdate.name }
                if (index != -1) {
                    currentList[index] = stockUpdate
                } else {
                    currentList.add(stockUpdate)
                }
                currentList.sortWith(Comparator { o1, o2 -> o2.price.compareTo(o1.price) })
                _stockUpdates.update { currentList }
            }
        }
    }


    fun stopWsConnection() {
        coroutineScope.launch {
            repository.disconnect()
            _isConnected.emit(false)
            _stockUpdates.emit(listOf())
        }

    }
}