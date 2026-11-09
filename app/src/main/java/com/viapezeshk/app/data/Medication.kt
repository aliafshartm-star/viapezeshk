package com.viapezeshk.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,          // اسم دارو (مثلاً متفورمین)
    val dosage: String,        // مقدار مصرف (مثلاً ۱ قرص)
    val time: String,          // ساعت مصرف (مثلاً "08:00")
    val isActive: Boolean = true // برای روشن/خاموش کردن یادآور
)