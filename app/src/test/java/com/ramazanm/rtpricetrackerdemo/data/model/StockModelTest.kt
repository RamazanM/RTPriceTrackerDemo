package com.ramazanm.rtpricetrackerdemo.data.model

import org.junit.Assert
import org.junit.Test

class StockModelTest {
    @Test
    fun `StockDTO stores data correctly`(){
        val stockDTO = StockDTO(
            name = "AMZN",
            price = 100.0,
            timestamp = 1234567890
        )
        Assert.assertEquals("AMZN", stockDTO.name)
        Assert.assertEquals(100.0, stockDTO.price, 0.001)
        Assert.assertEquals(1234567890, stockDTO.timestamp)
        }
    @Test
    fun `StockDTO has default values`(){
        val stockDTO = StockDTO()
        Assert.assertEquals("", stockDTO.name)
        Assert.assertEquals(0.0, stockDTO.price, 0.001)
        Assert.assertEquals(0, stockDTO.timestamp)
    }
    @Test
    fun `StockUi model stores data correctly`(){
        val stockUi = StockUi(
            name = "AMZN",
            price = 100.0,
            timestamp = 1234567890,
            indicator = StockIndicator.UP
        )
        Assert.assertEquals("AMZN", stockUi.name)
        Assert.assertEquals(100.0, stockUi.price, 0.001)
        Assert.assertEquals(1234567890, stockUi.timestamp)
        Assert.assertEquals(StockIndicator.UP, stockUi.indicator)
    }
    @Test
    fun `StockUi model has default values`(){
        val stockUi = StockUi()
        Assert.assertEquals("", stockUi.name)
        Assert.assertEquals(0.0, stockUi.price, 0.001)
        Assert.assertEquals(0, stockUi.timestamp)
        Assert.assertEquals(StockIndicator.NEUTRAL, stockUi.indicator)
    }
}