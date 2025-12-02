package com.ramazanm.rtpricetrackerdemo.viewmodel

import app.cash.turbine.test
import com.google.gson.Gson
import com.ramazanm.rtpricetrackerdemo.data.repository.EchoStockRepositoryImpl
import com.ramazanm.rtpricetrackerdemo.data.repository.StockRepository
import com.ramazanm.rtpricetrackerdemo.domain.StockCollectorUseCase
import com.ramazanm.rtpricetrackerdemo.presentation.StockViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModelTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: StockRepository
    private lateinit var viewModel: StockViewModel

    private lateinit var stockCollectorUseCase: StockCollectorUseCase


    @Before
    fun setup() = runTest {
        // Initialize the ViewModel before each test
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
            Gson()
        )
        stockCollectorUseCase= StockCollectorUseCase(repository)

        viewModel = StockViewModel(stockCollectorUseCase, UnconfinedTestDispatcher(testScheduler))
    }

    @After
    fun teardown() {
        viewModel.stopWsConnection()
        repository.disconnect()
        mockWebServer.shutdown()
    }


    @Test
    fun `startWsConnection() connects to the WebSocket`() = runTest {
        viewModel.startWsConnection()
        advanceUntilIdle()
        assert(viewModel.isConnected.value)
    }

    @Test
    fun `stopWsConnection() disconnects from the WebSocket`() = runTest {
        viewModel.stopWsConnection()
        assert(!viewModel.isConnected.value)
    }

    @Test
    fun `After startWsConnection() stockUpdates is updated`() = runTest {
        viewModel.startWsConnection()
        viewModel.stockUpdates.test {
            // Skip the initial empty list
            awaitItem()
            val item = awaitItem()
            println(item)
            assert(item.isNotEmpty())
            viewModel.stopWsConnection()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `After stopWsConnection() stockUpdates is empty`() = runTest {
        viewModel.startWsConnection()
        advanceUntilIdle()

        viewModel.stockUpdates.test {
            awaitItem() //Skip initial empty value
            awaitItem()
            viewModel.stopWsConnection()
            advanceUntilIdle()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
        advanceUntilIdle()
        println(viewModel.stockUpdates.value)
        assert(viewModel.stockUpdates.value.isEmpty())
    }

    @Test
    fun `After startWsConnection() stockUpdates is sorted by price`() = runTest {
        repository = EchoStockRepositoryImpl(
            UnconfinedTestDispatcher(testScheduler),
            mockWebServer.url("/").toString(),
            Gson()
        )
        stockCollectorUseCase= StockCollectorUseCase(repository)

        viewModel = StockViewModel(stockCollectorUseCase, UnconfinedTestDispatcher(testScheduler))
        viewModel.startWsConnection()
        viewModel.stockUpdates.test(timeout = 5.toDuration(DurationUnit.SECONDS)) {
            for (i in 1..10) { // Skip first ten items
                awaitItem()
            }
            val actualStockList = awaitItem()
            viewModel.stopWsConnection()
            advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
            val sortedStockUpdates = actualStockList.sortedByDescending { it.price }
            println(sortedStockUpdates)
            println(actualStockList)
            assert(actualStockList == sortedStockUpdates)
        }
    }
}