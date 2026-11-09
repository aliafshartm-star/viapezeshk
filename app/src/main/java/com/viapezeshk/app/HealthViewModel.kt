package com.viapezeshk.app

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viapezeshk.app.data.AppDatabase
import com.viapezeshk.app.data.HealthRecord
import com.viapezeshk.app.data.Medication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class HealthViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val medicationDao = database.medicationDao()
    private val healthRecordDao = database.healthRecordDao()
    private val appContext = application.applicationContext

    val medications = medicationDao.getAllMedications().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allHealthRecords = healthRecordDao.getAllRecords().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- توابع داروها ---
    fun addMedication(name: String, dosage: String, time: String) {
        viewModelScope.launch {
            val newMed = Medication(name = name, dosage = dosage, time = time)
            val id = medicationDao.insertMedication(newMed)
            scheduleAlarm(newMed.copy(id = id.toInt())) // تنظیم آلارم بعد از ذخیره
        }
    }

    fun toggleMedicationActive(medication: Medication) {
        viewModelScope.launch {
            val updatedMed = medication.copy(isActive = !medication.isActive)
            medicationDao.updateMedication(updatedMed)
            if (updatedMed.isActive) {
                scheduleAlarm(updatedMed)
            } else {
                cancelAlarm(updatedMed)
            }
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            medicationDao.deleteMedication(medication)
            cancelAlarm(medication)
        }
    }

    // --- توابع علائم حیاتی ---
    fun addHealthRecord(type: String, value: Float) {
        viewModelScope.launch {
            healthRecordDao.insertRecord(HealthRecord(type = type, value = value))
        }
    }

    fun deleteHealthRecord(record: HealthRecord) {
        viewModelScope.launch {
            healthRecordDao.deleteRecord(record)
        }
    }

    // ================= منطق تنظیم آلارم =================
    private fun scheduleAlarm(medication: Medication) {
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(appContext, AlarmReceiver::class.java).apply {
            putExtra("med_name", medication.name)
            putExtra("med_dosage", medication.dosage)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            medication.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // تبدیل ساعت رشته‌ای (مثلا "08:00") به ساعت و دقیقه
        val timeParts = medication.time.split(":")
        val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1) // اگر زمان گذشته، برای فردا تنظیم کن
            }
        }

        // تنظیم آلارم روزانه (تکرار هر روز)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelAlarm(medication: Medication) {
        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(appContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            medication.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}