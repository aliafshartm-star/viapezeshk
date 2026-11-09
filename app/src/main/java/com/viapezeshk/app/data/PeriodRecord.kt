package com.viapezeshk.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "period_records")
data class PeriodRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: Long,       // تاریخ شروع پریود (به میلی‌ثانیه)
    val cycleLength: Int = 28 // طول دوره قاعدگی کاربر (پیش‌فرض ۲۸ روز)
)