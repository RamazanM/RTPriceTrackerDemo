package com.ramazanm.rtpricetrackerdemo.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ramazanm.rtpricetrackerdemo.MainActivity
import com.ramazanm.rtpricetrackerdemo.data.model.StockUi
import com.ramazanm.rtpricetrackerdemo.domain.StockCollectorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stockCollectorUseCase: StockCollectorUseCase
) : ViewModel() {

    private val args = savedStateHandle.toRoute<MainActivity.Detail>()
    val name = args.name

    val stockUi: StateFlow<StockUi?> = stockCollectorUseCase.stockListFlow
        .map { stocks ->
            stocks.find { it.name == name }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}