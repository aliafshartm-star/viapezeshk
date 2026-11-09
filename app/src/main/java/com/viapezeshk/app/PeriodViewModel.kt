package com.viapezeshk.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viapezeshk.app.data.AppDatabase
import com.viapezeshk.app.data.PeriodRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class PeriodViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val periodDao = database.periodDao()

    // لیست رکوردهای پریود (جدیدترین اول)
    val periodRecords = periodDao.getAllRecords().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addPeriod(startDateMillis: Long, cycleLength: Int = 28) {
        viewModelScope.launch {
            periodDao.insertRecord(PeriodRecord(startDate = startDateMillis, cycleLength = cycleLength))
        }
    }

    fun deletePeriod(record: PeriodRecord) {
        viewModelScope.launch {
            periodDao.deleteRecord(record)
        }
    }

    // تابع محاسبه پیش‌بینی‌ها
    fun calculatePredictions(record: PeriodRecord): Map<String, Long> {
        val calendar = Calendar.getInstance()
        
        // تاریخ پریود بعدی
        calendar.timeInMillis = record.startDate
        calendar.add(Calendar.DAY_OF_YEAR, record.cycleLength)
        val nextPeriodDate = calendar.timeInMillis

        // تاریخ تخمک‌گذاری (معمولاً ۱۴ روز قبل از پریود بعدی)
        calendar.timeInMillis = nextPeriodDate
        calendar.add(Calendar.DAY_OF_YEAR, -14)
        val ovulationDate = calendar.timeInMillis

        // شروع پنجره باروری (۵ روز قبل از تخمک‌گذاری)
        calendar.timeInMillis = ovulationDate
        calendar.add(Calendar.DAY_OF_YEAR, -5)
        val fertileStart = calendar.timeInMillis

        // پایان پنجره باروری (۱ روز بعد از تخمک‌گذاری)
        calendar.timeInMillis = ovulationDate
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val fertileEnd = calendar.timeInMillis

        return mapOf(
            "nextPeriod" to nextPeriodDate,
            "ovulation" to ovulationDate,
            "fertileStart" to fertileStart,
            "fertileEnd" to fertileEnd
        )
    }

    // تابع کمکی برای محاسبه روزهای باقی‌مانده تا پریود بعدی
    fun daysUntilNextPeriod(nextPeriodDate: Long): Long {
        val today = Calendar.getInstance().timeInMillis
        if (nextPeriodDate < today) return 0
        val diff = nextPeriodDate - today
        return TimeUnit.MILLISECONDS.toDays(diff)
    }
}