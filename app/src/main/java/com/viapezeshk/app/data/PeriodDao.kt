package com.viapezeshk.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodDao {
    // گرفتن لیست تاریخ‌های پریود (جدیدترین اول)
    @Query("SELECT * FROM period_records ORDER BY startDate DESC")
    fun getAllRecords(): Flow<List<PeriodRecord>>

    // گرفتن آخرین پریود ثبت‌شده (برای پیش‌بینی)
    @Query("SELECT * FROM period_records ORDER BY startDate DESC LIMIT 1")
    suspend fun getLatestRecord(): PeriodRecord?

    // اضافه کردن رکورد جدید
    @Insert
    suspend fun insertRecord(record: PeriodRecord)

    // حذف رکورد
    @Delete
    suspend fun deleteRecord(record: PeriodRecord)
}