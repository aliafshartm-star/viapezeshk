package com.viapezeshk.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viapezeshk.app.data.AppDatabase
import com.viapezeshk.app.data.Drug
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class DrugViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.drugDao()

    // لیست داروها به صورت زنده از دیتابیس خونده میشه
    val drugs = dao.getAllDrugs().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}