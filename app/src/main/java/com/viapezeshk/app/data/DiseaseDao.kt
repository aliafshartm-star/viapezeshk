package com.viapezeshk.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiseaseDao {
    @Query("SELECT * FROM diseases")
    fun getAllDiseases(): Flow<List<Disease>>

    @Query("SELECT * FROM diseases WHERE category = :category")
    fun getDiseasesByCategory(category: String): Flow<List<Disease>>

    // دستور جدید برای جستجوی محلی
    @Query("SELECT * FROM diseases WHERE title LIKE '%' || :query || '%' OR subtitle LIKE '%' || :query || '%'")
    suspend fun searchLocalDiseases(query: String): List<Disease>

    @Query("SELECT * FROM diseases WHERE id = :id")
    fun getDiseaseById(id: String): Disease?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diseases: List<Disease>)
}