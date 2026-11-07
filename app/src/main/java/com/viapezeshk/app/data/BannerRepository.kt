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

    suspend fun fetchBanners(position: String, userCity: String): List<Banner> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(configUrl).build()
                val response = client.newCall(request).execute()
                
                if (response.isSuccessful) {
                    val jsonString = response.body?.string()
                    if (!jsonString.isNullOrEmpty()) {
                        val config = gson.fromJson(jsonString, Config::class.java)
                        
                        // فیلتر کردن بنرها:
                        // 1. باید فعال باشن
                        // 2. موقعیت مکانیشون با این صفحه یکی باشه (مثلا home_main)
                        // 3. شهرشون یا خالی باشه (سراسری) یا با شهر کاربر یکی باشه
                        return@withContext config.banners?.filter { banner ->
                            val isActive = banner.active
                            val isPositionMatch = banner.position == position
                            val cities = banner.targeting?.cities ?: emptyList()
                            val isCityMatch = cities.isEmpty() || cities.contains(userCity) || userCity == "همهٔ ایران"
                            
                            isActive && isPositionMatch && isCityMatch
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