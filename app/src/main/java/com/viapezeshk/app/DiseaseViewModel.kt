package com.viapezeshk.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viapezeshk.app.data.AppDatabase
import com.viapezeshk.app.data.Disease
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    val diseases = dao.getAllDiseases().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // وضعیت‌های جستجو
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
                // 2. جستجوی همزمان در ویکی‌پدیا و PubMed
                val (wikiResult, pubmedResult) = coroutineScope {
                    val wikiDeferred = async { fetchFromWikipedia(query) }
                    val pubmedDeferred = async { fetchFromPubMed(query) }
                    Pair(wikiDeferred.await(), pubmedDeferred.await())
                }

                if (wikiResult != null || pubmedResult != null) {
                    var combinedResult = ""
                    
                    if (wikiResult != null) {
                        combinedResult += "📖 خلاصه عمومی (ویکی‌پدیا):\n$wikiResult\n\n"
                    }
                    
                    if (pubmedResult != null) {
                        combinedResult += "🔬 مقالات علمی مرتبط (PubMed):\n$pubmedResult"
                    }

                    _searchResult.value = combinedResult.trim()
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

    private suspend fun fetchFromPubMed(query: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. گرفتن لیست ID مقالات از PubMed
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val searchUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=$encodedQuery&retmode=json&retmax=3"
                
                val client = OkHttpClient()
                val searchRequest = Request.Builder().url(searchUrl).build()
                val searchResponse = client.newCall(searchRequest).execute()

                if (!searchResponse.isSuccessful) return@withContext null

                val searchJson = searchResponse.body?.string() ?: return@withContext null
                val idList = JsonParser.parseString(searchJson)
                    .asJsonObject.getAsJsonObject("esearchresult")
                    .getAsJsonArray("idlist")

                if (idList.size() == 0) return@withContext null

                // 2. گرفتن خلاصه مقالات بر اساس ID
                val ids = idList.joinToString(",") { it.asString }
                val summaryUrl = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id=$ids&retmode=json"
                
                val summaryRequest = Request.Builder().url(summaryUrl).build()
                val summaryResponse = client.newCall(summaryRequest).execute()

                if (summaryResponse.isSuccessful) {
                    val summaryJson = summaryResponse.body?.string() ?: return@withContext null
                    val resultObj = JsonParser.parseString(summaryJson)
                        .asJsonObject.getAsJsonObject("result")
                    
                    val titles = mutableListOf<String>()
                    for (i in 0 until idList.size()) {
                        val id = idList.get(i).asString
                        if (resultObj.has(id)) {
                            val title = resultObj.getAsJsonObject(id).get("title").asString
                            titles.add("• $title")
                        }
                    }
                    if (titles.isNotEmpty()) {
                        return@withContext titles.joinToString("\n")
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