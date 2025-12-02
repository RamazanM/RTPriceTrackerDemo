# Real Time Price Tracker Demo

An Android application demonstrating real-time stock price tracking using WebSocket simulation.

## Features

*   **Real-time Updates:** Simulates live stock price changes.
*   **Dynamic Sorting:** Stocks are automatically sorted by price in descending order.
*   **Connection Control:** Toggle the WebSocket connection to start/stop updates.
*   **Detailed View:** Click on any stock to view its details (Name, Price, Description, Trend).
*   **Visual Indicators:** Color-coded indicators for price movement (Up/Down/Neutral) and connection status.

## Tech Stack

*   **Kotlin:** Programming language of this project.
*   **Jetpack Compose:** Modern toolkit for building native UI.
*   **Hilt:** Dependency Injection.
*   **Coroutines & Flow:** Asynchronous programming and reactive data streams.
*   **OkHttp:** WebSocket client (simulated echo loop in this demo).
*   **Navigation Compose:** Type-safe navigation between screens.
*   **Clean Architecture:** Separation of concerns (Data, Domain, Presentation).

## Architecture

The app follows Clean Architecture principles:

*   **Data Layer:** Handles data fetching (simulated WebSocket repository `EchoStockRepositoryImpl`).
*   **Domain Layer:** Contains business logic (`StockCollectorUseCase`).
*   **Presentation Layer:** UI components (Compose) and ViewModels (`StockViewModel`, `DetailViewModel`).

## Setup & Run

1.  Clone the repository.
2.  Open in Android Studio.
3.  Sync Gradle project.
4.  Build and run on an emulator or physical device.

## Note

To demonstrate real-time features without a live external server, this demo generates random stock data internally and loops it back as if it were a live WebSocket feed. 
