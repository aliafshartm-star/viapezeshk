package com.viapezeshk.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {
    // گرفتن رکوردهای یک نوع خاص (مثلاً فقط قند خون) به ترتیب زمان (جدیدترین اول)
    @Query("SELECT * FROM health_records WHERE type = :type ORDER BY timestamp DESC")
    fun getRecordsByType(type: String): Flow<List<HealthRecord>>

    // گرفتن تمام رکوردها (برای بخش تاریخچه کلی)
    @Query("SELECT * FROM health_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<HealthRecord>>

    // اضافه کردن رکورد جدید
    @Insert
    suspend fun insertRecord(record: HealthRecord)

    // حذف رکورد
    @Delete
    suspend fun deleteRecord(record: HealthRecord)
}