package com.viapezeshk.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viapezeshk.app.data.AppDatabase
import com.viapezeshk.app.data.Disease
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import com.google.gson.JsonParser

class DiseaseViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.diseaseDao()

    // دیتابیس محلی
    val diseases = dao.getAllDiseases().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // وضعیت‌های جستجوی هوشمند (آنلاین/آفلاین)
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchResult = MutableStateFlow<String?>(null)
    val searchResult: StateFlow<String?> = _searchResult

    private val _isOnlineResult = MutableStateFlow(false)
    val isOnlineResult: StateFlow<Boolean> = _isOnlineResult

    fun smartSearch(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _isSearching.value = true
            _searchResult.value = null
            _isOnlineResult.value = false

            // 1. جستجو در دیتابیس محلی
            val localResult = dao.searchLocalDiseases(query)
            if (localResult.isNotEmpty()) {
                _searchResult.value = localResult[0].content
                _isOnlineResult.value = false
            } else {
                // 2. اگر در دیتابیس نبود، جستجو در ویکی‌پدیا
                val onlineResult = fetchFromWikipedia(query)
                if (onlineResult != null) {
                    _searchResult.value = onlineResult
                    _isOnlineResult.value = true
                } else {
                    _searchResult.value = "متأسفانه اطلاعاتی برای این مورد یافت نشد. لطفاً با پزشک مشورت کنید."
                }
            }
            _isSearching.value = false
        }
    }

    private suspend fun fetchFromWikipedia(query: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val url = "https://fa.wikipedia.org/api/rest_v1/page/summary/$encodedQuery"
                
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonString = response.body?.string()
                    val jsonObject = JsonParser.parseString(jsonString).asJsonObject
                    if (jsonObject.has("extract")) {
                        return@withContext jsonObject.get("extract").asString
                    }
                }
                null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}