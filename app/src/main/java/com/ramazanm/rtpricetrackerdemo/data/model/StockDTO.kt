package com.ramazanm.rtpricetrackerdemo.data.model

data class StockDTO(val name: String = "", val price: Double = 0.0, val timestamp: Long = 0, val errorMsg:String?=null)
