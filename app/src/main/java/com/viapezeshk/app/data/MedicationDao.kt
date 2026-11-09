package com.viapezeshk.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    // گرفتن لیست همه داروها
    @Query("SELECT * FROM medications")
    fun getAllMedications(): Flow<List<Medication>>

    // اضافه کردن داروی جدید و برگردوندن آیدی اون
    @Insert
    suspend fun insertMedication(medication: Medication): Long // <--- این خط اصلاح شد

    // آپدیت کردن وضعیت دارو (روشن/خاموش کردن یادآور)
    @Update
    suspend fun updateMedication(medication: Medication)

    // حذف دارو
    @Delete
    suspend fun deleteMedication(medication: Medication)
}