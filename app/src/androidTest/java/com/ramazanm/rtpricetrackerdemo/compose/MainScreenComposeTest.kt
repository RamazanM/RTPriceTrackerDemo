package com.ramazanm.rtpricetrackerdemo.compose

import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.ramazanm.rtpricetrackerdemo.data.model.StockIndicator
import com.ramazanm.rtpricetrackerdemo.data.model.StockUi
import com.ramazanm.rtpricetrackerdemo.presentation.StockViewModel
import com.ramazanm.rtpricetrackerdemo.ui.screen.MainScreen
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var viewModel: StockViewModel = mockk<StockViewModel>()

    @Before
    fun setUp() = runTest {
        val isConnected = MutableStateFlow(false)
        coEvery { viewModel.stockUpdates } returns MutableStateFlow(
            listOf(
                StockUi(
                    name = "AAPL",
                    price = 300.0,
                    indicator = StockIndicator.NEUTRAL,
                    details = "Apple Inc."
                ),
                StockUi(
                    name = "MSFT",
                    price = 200.0,
                    indicator = StockIndicator.DOWN,
                    details = "Microsoft Corporation"
                ),
                StockUi(
                    name = "AMZN",
                    price = 100.0,
                    indicator = StockIndicator.UP,
                    details = "Amazon.com, Inc."
                )
            )
        ).asStateFlow()
        coEvery { viewModel.startWsConnection() } answers { isConnected.value = true }
        coEvery { viewModel.stopWsConnection() } answers { isConnected.value = false }
        coEvery { viewModel.isConnected } returns isConnected
        composeTestRule.setContent {
            MainScreen(rememberNavController(), viewModel)
        }
    }

    @Test
    fun lazyColumn_is_shown_in_the_UI() {
        composeTestRule.onNodeWithTag("LazyColumn").assertExists()

    }

    @Test
    fun each_row_in_LazyColumn_has_symbol_name_current_price_price_change_indicator() {
        composeTestRule.onNodeWithTag("LazyColumn", useUnmergedTree = true).onChildAt(0)
            .onChildAt(0).assertTextContains("AAPL")
        composeTestRule.onNodeWithTag("LazyColumn", useUnmergedTree = true).onChildAt(0)
            .onChildAt(1).assertTextContains("$300.00")
        composeTestRule.onNodeWithTag("LazyColumn", useUnmergedTree = true).onChildAt(0)
            .onChildAt(2).assertContentDescriptionContains("Neutral")

    }

    @Test
    fun items_in_LazyColumn_are_sorted_by_price_descending() {
        composeTestRule.onNodeWithTag("LazyColumn",useUnmergedTree = true).onChildAt(0).onChildAt(0)
            .assertTextContains("AAPL")
        composeTestRule.onNodeWithTag("LazyColumn",useUnmergedTree = true).onChildAt(1).onChildAt(0)
            .assertTextContains("MSFT")
        composeTestRule.onNodeWithTag("LazyColumn",useUnmergedTree = true).onChildAt(2).onChildAt(0)
            .assertTextContains("AMZN")
    }

    @Test
    fun topbar_has_connection_indicator() {
        composeTestRule.onNodeWithTag("ConnectionIndicator").assertExists()
    }

    @Test
    fun topbar_has_toggle_button_for_start_stop_server() {
        composeTestRule.onNodeWithTag("ToggleServerButton").assertExists()
    }

    @Test
    fun toggle_button_on_topbar_changes_the_indicator() {
        composeTestRule.onNodeWithTag("ToggleServerButton").performClick()
        composeTestRule.onNodeWithTag("ConnectionIndicator")
            .assertContentDescriptionEquals("Server is active")
        composeTestRule.onNodeWithTag("ToggleServerButton").performClick()
        composeTestRule.onNodeWithTag("ConnectionIndicator")
            .assertContentDescriptionEquals("Server deactivated")
    }

    @Test
    fun toggle_button_on_topbar_toggles_start_stop_the_server() {
        composeTestRule.onNodeWithTag("ToggleServerButton").performClick()
        coVerify { viewModel.startWsConnection() }
        composeTestRule.onNodeWithTag("ToggleServerButton").performClick()
        coVerify { viewModel.stopWsConnection() }
    }
}