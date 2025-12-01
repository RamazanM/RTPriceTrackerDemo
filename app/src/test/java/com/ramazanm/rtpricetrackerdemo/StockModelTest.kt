package com.ramazanm.rtpricetrackerdemo

import com.ramazanm.rtpricetrackerdemo.model.StockDTO
import com.ramazanm.rtpricetrackerdemo.model.StockIndicator
import com.ramazanm.rtpricetrackerdemo.model.StockUi
import org.junit.Assert.assertEquals
import org.junit.Test

class StockModelTest {
    @Test
    fun `StockDTO stores data correctly`(){
        val stockDTO = StockDTO(
            name = "AMZN",
            price = 100.0,
            timestamp = 1234567890
        )
        assertEquals("AMZN", stockDTO.name)
        assertEquals(100.0, stockDTO.price, 0.001)
        assertEquals(1234567890, stockDTO.timestamp)
        }
    @Test
    fun `StockDTO has default values`(){
        val stockDTO = StockDTO()
        assertEquals("", stockDTO.name)
        assertEquals(0.0, stockDTO.price, 0.001)
        assertEquals(0, stockDTO.timestamp)
    }
    @Test
    fun `StockUi model stores data correctly`(){
        val stockUi = StockUi(
            name = "AMZN",
            price = 100.0,
            timestamp = 1234567890,
            indicator = StockIndicator.UP
        )
        assertEquals("AMZN", stockUi.name)
        assertEquals(100.0, stockUi.price, 0.001)
        assertEquals(1234567890, stockUi.timestamp)
        assertEquals(StockIndicator.UP, stockUi.indicator)
    }
    @Test
    fun `StockUi model has default values`(){
        val stockUi = StockUi()
        assertEquals("", stockUi.name)
        assertEquals(0.0, stockUi.price, 0.001)
        assertEquals(0, stockUi.timestamp)
        assertEquals(StockIndicator.NEUTRAL, stockUi.indicator)
    }
}