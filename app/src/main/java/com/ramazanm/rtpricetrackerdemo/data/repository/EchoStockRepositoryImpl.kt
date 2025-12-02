package com.ramazanm.rtpricetrackerdemo.data.repository

import com.google.gson.Gson
import com.ramazanm.rtpricetrackerdemo.data.model.StockDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class EchoStockRepositoryImpl(
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val baseUrl: String,
    private val gson: Gson

) : StockRepository(ioDispatcher = ioDispatcher) {

    private val _stockUpdates = MutableSharedFlow<StockDTO>()

    //Flow for emitting each stock update
    override val stockUpdates: Flow<StockDTO> = _stockUpdates

    private var echoJob: Job? = null

    private lateinit var webSocket: WebSocket

    override fun connect() {
        if (::webSocket.isInitialized) {
            webSocket.close(1000, "New Connection")
        }
        webSocket = OkHttpClient().newWebSocket(
            Request.Builder().url(baseUrl).build(),
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    startEchoLoop()
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    ioCoroutineScope.launch {
                        val stockDTO = gson.fromJson(text, StockDTO::class.java)
                        _stockUpdates.emit(stockDTO)
                    }
                }

                override fun onFailure(
                    webSocket: WebSocket, t: Throwable, response: Response?
                ) {
                    super.onFailure(webSocket, t, response)
                    ioCoroutineScope.launch {
                        _stockUpdates.emit(StockDTO(errorMsg = "Failed!"))
                        stopEchoLoop()
                    }
                }

                override fun onClosed(
                    webSocket: WebSocket, code: Int, reason: String
                ) {
                    super.onClosed(webSocket, code, reason)
                    ioCoroutineScope.launch {
                        _stockUpdates.emit(StockDTO(errorMsg = "Closed!"))
                        stopEchoLoop()
                    }
                }
            })
    }

    override fun disconnect() {
        stopEchoLoop()
        if (::webSocket.isInitialized) {
            webSocket.cancel()
            webSocket.close(1000, null)
        }
    }

    // Starts the main loop for continuously feeding random values to the echo server
    fun startEchoLoop() {
        if (echoJob?.isActive == true) {
            return
        }

        echoJob = ioCoroutineScope.launch {
            while (isActive) {
                for (symbol in symbolList)
                    sendStockUpdate(generateRandomStockResponse(symbol.first,symbol.second))
                delay(2000L)
            }
        }
    }

    fun stopEchoLoop() {
        echoJob?.cancel()
    }

    fun sendStockUpdate(updatedValue: StockDTO) {
        webSocket.send(gson.toJson(updatedValue))
    }

    private fun generateRandomStockResponse(name: String, description: String): StockDTO {
        return StockDTO(name, (Math.random() * 1000),description)
    }

    companion object {
        //Predefined list of stock symbols used to generate random data.
        val symbolList = listOf(
            "AAPL" to "Apple Inc.",
            "MSFT" to "Microsoft Corporation",
            "NVDA" to "NVIDIA Corporation",
            "AMZN" to "Amazon.com, Inc.",
            "GOOGL" to "Alphabet Inc. Class A",
            "GOOG" to "Alphabet Inc. Class C",
            "META" to "Meta Platforms, Inc.",
            "TSLA" to "Tesla, Inc.",
            "AVGO" to "Broadcom Inc.",
            "ASML" to "ASML Holding N.V.",
            "NFLX" to "Netflix, Inc.",
            "COST" to "Costco Wholesale Corporation",
            "ADBE" to "Adobe Inc.",
            "AMD" to "Advanced Micro Devices, Inc.",
            "CMCSA" to "Comcast Corporation",
            "CSCO" to "Cisco Systems, Inc.",
            "PEP" to "PepsiCo, Inc.",
            "INTC" to "Intel Corporation",
            "T-MUS" to "T-Mobile US, Inc.",
            "PYPL" to "PayPal Holdings, Inc.",
            "QCOM" to "Qualcomm Incorporated",
            "SBUX" to "Starbucks Corporation",
            "ISRG" to "Intuitive Surgical, Inc.",
            "MU" to "Micron Technology, Inc.",
            "AMGN" to "Amgen Inc."
        )
    }
}
