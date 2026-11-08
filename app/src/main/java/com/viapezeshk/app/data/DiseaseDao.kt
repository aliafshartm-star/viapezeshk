package com.viapezeshk.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiseaseDao {
    // گرفتن همه بیماری‌ها
    @Query("SELECT * FROM diseases")
    fun getAllDiseases(): Flow<List<Disease>>

    // گرفتن بیماری‌های یک دسته‌بندی خاص (مثلا مغز و اعصاب)
    @Query("SELECT * FROM diseases WHERE category = :category")
    fun getDiseasesByCategory(category: String): Flow<List<Disease>>

    // گرفتن یه بیماری خاص با آیدی
    @Query("SELECT * FROM diseases WHERE id = :id")
    fun getDiseaseById(id: String): Disease?

    // اضافه کردن لیست بیماری‌ها به دیتابیس
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diseases: List<Disease>)
}