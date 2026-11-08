package com.viapezeshk.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrugDao {
    @Query("SELECT * FROM drugs")
    fun getAllDrugs(): Flow<List<Drug>>

    @Query("SELECT * FROM drugs WHERE id = :id")
    fun getDrugById(id: String): Drug?

    // دستورات جدید برای نشان‌شده‌ها
    @Query("SELECT * FROM drugs WHERE isBookmarked = 1")
    fun getBookmarkedDrugs(): Flow<List<Drug>>

    @Query("UPDATE drugs SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: String, isBookmarked: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(drugs: List<Drug>)
}