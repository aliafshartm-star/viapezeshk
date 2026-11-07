package com.viapezeshk.app.data

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class BannerRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    
    // آدرس فایل JSON توی گیت‌هاب (یوزرنیم و ریپازیتوری خودت)
    private val configUrl = "https://raw.githubusercontent.com/aliafshartm-star/viapezeshk/main/data/config.json"

    suspend fun fetchBanners(): List<Banner> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(configUrl).build()
                val response = client.newCall(request).execute()
                
                if (response.isSuccessful) {
                    val jsonString = response.body?.string()
                    if (!jsonBodyNullOrEmpty(jsonString)) {
                        val config = gson.fromJson(jsonString, Config::class.java)
                        // فقط بنرهایی رو برمی‌گردونیم که فعال هستن
                        return@withContext config.banners?.filter { it.active } ?: emptyList()
                    }
                }
                emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // در صورت قطعی اینترنت، لیست خالی برمی‌گرده تا اپ کرش نکنه
            }
        }
    }

    private fun jsonBodyNullOrEmpty(json: String?): Boolean {
        return json.isNullOrEmpty()
    }
}