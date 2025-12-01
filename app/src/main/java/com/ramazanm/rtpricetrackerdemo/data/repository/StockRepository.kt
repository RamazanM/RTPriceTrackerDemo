package com.ramazanm.rtpricetrackerdemo.data.repository

import com.ramazanm.rtpricetrackerdemo.data.model.StockDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

abstract class StockRepository(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    val ioCoroutineScope = CoroutineScope(ioDispatcher)
    abstract val stockUpdates: Flow<StockDTO>
    abstract fun connect()
    abstract fun disconnect()
}