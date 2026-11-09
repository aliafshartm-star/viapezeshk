package com.viapezeshk.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_records")
data class HealthRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,          // نوع رکورد: "glucose" (قند)، "bp_sys" (فشار سیستول)، "bp_dia" (فشار دیاستول)، "weight" (وزن)
    val value: Float,          // مقدار عددی
    val timestamp: Long = System.currentTimeMillis() // زمان ثبت
)