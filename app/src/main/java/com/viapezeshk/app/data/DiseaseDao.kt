package com.viapezeshk.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiseaseDao {
    // گرفتن همه بیماری‌ها برای نمایش در لیست
    @Query("SELECT * FROM diseases")
    fun getAllDiseases(): Flow<List<Disease>>

    // گرفتن یه بیماری خاص با آیدی (برای صفحه جزئیات)
    @Query("SELECT * FROM diseases WHERE id = :id")
    fun getDiseaseById(id: String): Disease?

    // اضافه کردن لیست بیماری‌ها به دیتابیس (وقتی اپ برای اولین بار باز میشه)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diseases: List<Disease>)
}