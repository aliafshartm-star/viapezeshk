package com.viapezeshk.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // وضعیت حالت تاریک
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    // وضعیت اندازه فونت (0=کوچک، 1=متوسط، 2=بزرگ)
    private val _fontSize = MutableStateFlow(prefs.getInt("font_size", 1))
    val fontSize: StateFlow<Int> = _fontSize

    // وضعیت شهر
    private val _userCity = MutableStateFlow(prefs.getString("user_city", "همهٔ ایران") ?: "همهٔ ایران")
    val userCity: StateFlow<String> = _userCity

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("dark_mode", enabled).apply()
    }

    fun setFontSize(size: Int) {
        _fontSize.value = size
        prefs.edit().putInt("font_size", size).apply()
    }

    fun setCity(city: String) {
        _userCity.value = city
        prefs.edit().putString("user_city", city).apply()
    }

    // ---- قابلیت‌های جدید برای صفحه خوش‌آمدگویی ----
    fun hasSeenDisclaimer(): Boolean {
        return prefs.getBoolean("has_seen_disclaimer", false)
    }

    fun setHasSeenDisclaimer() {
        prefs.edit().putBoolean("has_seen_disclaimer", true).apply()
    }
}