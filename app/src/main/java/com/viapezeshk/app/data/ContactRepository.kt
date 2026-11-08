package com.viapezeshk.app.data

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ContactRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    
    // آدرس فایل contact.json در گیت‌هاب
    private val contactUrl = "https://raw.githubusercontent.com/aliafshartm-star/viapezeshk/main/data/contact.json"

    suspend fun fetchContacts(): List<ContactItem> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(contactUrl).build()
                val response = client.newCall(request).execute()
                
                if (response.isSuccessful) {
                    val jsonString = response.body?.string()
                    if (!jsonString.isNullOrEmpty()) {
                        val config = gson.fromJson(jsonString, ContactConfig::class.java)
                        return@withContext config.contacts ?: emptyList()
                    }
                }
                emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // در صورت نبود اینترنت یا نبود فایل، لیست خالی برمی‌گرداند
            }
        }
    }
}