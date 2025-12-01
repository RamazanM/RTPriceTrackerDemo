package com.ramazanm.rtpricetrackerdemo.model

data class StockUi(
    val name: String = "",
    val price: Double = 0.0,
    val timestamp: Int = 0,
    val indicator: StockIndicator = StockIndicator.NEUTRAL
)

enum class StockIndicator {
    UP, DOWN, NEUTRAL
}