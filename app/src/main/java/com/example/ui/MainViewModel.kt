package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.KeyVaultRepository
import com.example.data.SavedKey
import com.example.data.WorkflowConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreen {
  HOME,
  BROWSER
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = KeyVaultRepository(application)

  val savedKeys: StateFlow<List<SavedKey>> = repository.savedKeys

  private val _currentScreen = MutableStateFlow(AppScreen.HOME)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _browserUrl = MutableStateFlow(WorkflowConstants.INITIAL_WEBSITE_URL)
  val browserUrl: StateFlow<String> = _browserUrl.asStateFlow()

  private val _step1Completed = MutableStateFlow(false)
  val step1Completed: StateFlow<Boolean> = _step1Completed.asStateFlow()

  private val _step2Completed = MutableStateFlow(false)
  val step2Completed: StateFlow<Boolean> = _step2Completed.asStateFlow()

  fun startWorkflow() {
    _browserUrl.value = WorkflowConstants.INITIAL_WEBSITE_URL
    _step1Completed.value = true
    _currentScreen.value = AppScreen.BROWSER
  }

  fun openKeyPageDirectly() {
    _browserUrl.value = WorkflowConstants.KEY_PAGE_URL
    _step2Completed.value = true
    _currentScreen.value = AppScreen.BROWSER
  }

  fun openCustomUrl(url: String) {
    if (WorkflowConstants.isSecureUrl(url)) {
      _browserUrl.value = url
      _currentScreen.value = AppScreen.BROWSER
    }
  }

  fun closeBrowser() {
    _currentScreen.value = AppScreen.HOME
  }

  fun saveKey(keyText: String, source: String = WorkflowConstants.KEY_PAGE_URL) {
    viewModelScope.launch {
      repository.addKey(keyText, source)
      _step2Completed.value = true
    }
  }

  fun deleteKey(id: String) {
    viewModelScope.launch {
      repository.deleteKey(id)
    }
  }

  fun clearAllKeys() {
    viewModelScope.launch {
      repository.clearAll()
    }
  }
}
