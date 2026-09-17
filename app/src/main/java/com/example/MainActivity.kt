package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.data.local.WatchlistManager
import com.example.ui.MainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private lateinit var watchlistManager: WatchlistManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    watchlistManager = WatchlistManager(applicationContext)

    setContent {
      MyApplicationTheme(darkTheme = true) {
        MainScreen(
          watchlistManager = watchlistManager,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}

