package com.viapezeshk.app.data

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class BannerRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    
    private val configUrl = "https://raw.githubusercontent.com/aliafshartm-star/viapezeshk/main/data/config.json"

    suspend fun fetchBanners(position: String): List<Banner> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(configUrl).build()
                val response = client.newCall(request).execute()
                
                if (response.isSuccessful) {
                    val jsonString = response.body?.string()
                    if (!jsonString.isNullOrEmpty()) {
                        val config = gson.fromJson(jsonString, Config::class.java)
                        // فقط بنرهایی رو برمی‌گردونیم که فعال هستن و مربوط به این جایگاه (position) هستن
                        return@withContext config.banners?.filter { 
                            it.active && it.position == position 
                        }?.sortedBy { it.priority } ?: emptyList()
                    }
                }
                emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}