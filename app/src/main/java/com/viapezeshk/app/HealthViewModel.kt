package com.viapezeshk.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viapezeshk.app.data.AppDatabase
import com.viapezeshk.app.data.HealthRecord
import com.viapezeshk.app.data.Medication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HealthViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val medicationDao = database.medicationDao()
    private val healthRecordDao = database.healthRecordDao()

    // لیست داروها به صورت زنده
    val medications = medicationDao.getAllMedications().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // لیست تمام رکوردهای سلامت (قند، فشار، وزن) به صورت زنده
    val allHealthRecords = healthRecordDao.getAllRecords().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- توابع مربوط به داروها ---
    fun addMedication(name: String, dosage: String, time: String) {
        viewModelScope.launch {
            medicationDao.insertMedication(Medication(name = name, dosage = dosage, time = time))
        }
    }

    fun toggleMedicationActive(medication: Medication) {
        viewModelScope.launch {
            medicationDao.updateMedication(medication.copy(isActive = !medication.isActive))
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            medicationDao.deleteMedication(medication)
        }
    }

    // --- توابع مربوط به ثبت علائم حیاتی ---
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
}