package com.example.weekcalendar.ui.theme

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: Context) : ViewModel() {
    private val _currentLanguage = MutableStateFlow(0)
    val currentLanguage: StateFlow<Int> = _currentLanguage.asStateFlow()

    private val _isMondayStart = MutableStateFlow(true)
    val isMondayStart: StateFlow<Boolean> = _isMondayStart.asStateFlow()

    init {
        viewModelScope.launch {
            loadLanguageID(context).collect { _currentLanguage.value = it }
        }
        viewModelScope.launch {
            loadStartDay(context).collect { _isMondayStart.value = it }
        }
    }

    fun saveLanguage(languageId: Int) {
        viewModelScope.launch {
            saveLanguageId(context, languageId)
            _currentLanguage.value = languageId
        }
    }

    fun saveStartDay(isMonday: Boolean) {
        viewModelScope.launch {
            saveStartDay(context, isMonday)
            _isMondayStart.value = isMonday
        }
    }
}