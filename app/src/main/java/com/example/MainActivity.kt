package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.browser.SecureBrowserScreen
import com.example.ui.home.HomeScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        val currentScreen by viewModel.currentScreen.collectAsState()
        val browserUrl by viewModel.browserUrl.collectAsState()
        val savedKeys by viewModel.savedKeys.collectAsState()
        val step1Done by viewModel.step1Completed.collectAsState()
        val step2Done by viewModel.step2Completed.collectAsState()

        Surface(
          modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
          color = DarkBackground
        ) {
          AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
              if (targetState == AppScreen.BROWSER) {
                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                  slideOutHorizontally { width -> -width / 3 } + fadeOut()
                )
              } else {
                (slideInHorizontally { width -> -width / 3 } + fadeIn()).togetherWith(
                  slideOutHorizontally { width -> width } + fadeOut()
                )
              }
            },
            label = "screen_transition"
          ) { screen ->
            when (screen) {
              AppScreen.HOME -> {
                HomeScreen(
                  onStartWorkflow = { viewModel.startWorkflow() },
                  onOpenKeyPageDirectly = { viewModel.openKeyPageDirectly() },
                  savedKeys = savedKeys,
                  step1Done = step1Done,
                  step2Done = step2Done,
                  onDeleteKey = { id -> viewModel.deleteKey(id) },
                  onClearAllKeys = { viewModel.clearAllKeys() }
                )
              }

              AppScreen.BROWSER -> {
                SecureBrowserScreen(
                  initialUrl = browserUrl,
                  onClose = { viewModel.closeBrowser() },
                  onKeySaved = { key -> viewModel.saveKey(key, browserUrl) }
                )
              }
            }
          }
        }
      }
    }
  }
}
