package com.ramazanm.rtpricetrackerdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ramazanm.rtpricetrackerdemo.ui.screen.DetailScreen
import com.ramazanm.rtpricetrackerdemo.ui.screen.MainScreen
import com.ramazanm.rtpricetrackerdemo.ui.theme.RTPriceTrackerDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Serializable
    object Feed
    @Serializable
    data class Detail(val name: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            RTPriceTrackerDemoTheme {
                NavHost(navController = navController, startDestination = Feed) {
                    composable<Feed> { MainScreen(navController) }
                    composable<Detail> {
                        DetailScreen(navController)
                    }
                }
            }
        }
    }
}