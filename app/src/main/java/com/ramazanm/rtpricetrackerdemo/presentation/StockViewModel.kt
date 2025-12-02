package com.ramazanm.rtpricetrackerdemo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramazanm.rtpricetrackerdemo.di.IoDispatcher
import com.ramazanm.rtpricetrackerdemo.domain.StockCollectorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val stockCollectorUseCase: StockCollectorUseCase,
    @IoDispatcher dispatcher: CoroutineDispatcher? = null
) : ViewModel() {

    val coroutineScope = dispatcher?.let { CoroutineScope(it) } ?: viewModelScope

    private val _isConnected = MutableStateFlow(false)

    val isConnected = _isConnected.asStateFlow()

    val stockUpdates = stockCollectorUseCase.stockListFlow

    fun startWsConnection() {
        coroutineScope.launch {
            _isConnected.emit(true)
            stockCollectorUseCase.startCollecting()
        }
    }


    fun stopWsConnection() {
        coroutineScope.launch {
            _isConnected.emit(false)
            stockCollectorUseCase.stopCollecting()
        }
    }
}