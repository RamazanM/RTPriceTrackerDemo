package com.ramazanm.rtpricetrackerdemo.data.repository

import app.cash.turbine.test
import com.google.gson.Gson
import com.ramazanm.rtpricetrackerdemo.data.model.StockDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalCoroutinesApi::class)
class StockRepositoryTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: StockRepository
    private val gson = Gson()

    @Before
    fun setup() = runTest {
        val echoListener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                // Server logic: Send the received message back to the client (Echo)
                webSocket.send(text)
            }
        }
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockWebServer.enqueue(MockResponse().withWebSocketUpgrade(echoListener))
        repository = EchoStockRepositoryImpl(
            UnconfinedTestDispatcher(testScheduler),
            mockWebServer.url("/").toString(),
            gson
        )


    }

    @After
    fun teardown() {
        repository.disconnect()
        mockWebServer.shutdown()
    }

    @Test
    fun `connect method works without exception`() = runTest {
        repository.connect()
    }

    @Test
    fun `disconnect method works without exception`() {
        repository.disconnect()
    }

    @Test
    fun `stockUpdates flow emits the echoed data`() = runTest {
        val repositoryImpl = EchoStockRepositoryImpl(
            UnconfinedTestDispatcher(testScheduler),
            mockWebServer.url("/").toString(),
            gson
        )
        repositoryImpl.connect()
        repositoryImpl.stockUpdates.test(timeout = 10.toDuration(DurationUnit.SECONDS)) {
            repositoryImpl.sendStockUpdate(
                StockDTO(
                    "Test", 100.0,  "Test description",Date().time,
                )
            )
            val item = awaitItem()
            assertEquals("Test", item.name)
            repositoryImpl.disconnect()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `stockUpdates flow emits disconnection message`() = runTest {
        repository.connect()
        repository.stockUpdates.test {
            repository.disconnect()
            advanceUntilIdle()
            val response = awaitItem()
            assertEquals("Failed!", response.errorMsg)
            cancelAndIgnoreRemainingEvents()

        }
    }

    @Test
    fun `after connection established flow emits new data every two seconds`() = runBlocking {
        repository.stockUpdates.test(timeout = 10.toDuration(DurationUnit.SECONDS)) {
            repository.connect()
            for (i in 0..10) {
                val item = awaitItem()
                assertEquals(EchoStockRepositoryImpl.symbolList[i].first, item.name)
            }
            repository.disconnect()
            cancelAndIgnoreRemainingEvents()

        }
    }


}