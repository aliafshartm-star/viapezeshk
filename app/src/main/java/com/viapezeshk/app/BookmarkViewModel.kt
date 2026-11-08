package com.viapezeshk.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.viapezeshk.app.data.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val diseaseDao = database.diseaseDao()
    private val drugDao = database.drugDao()

    // لیست بیماری‌های نشان‌شده
    val bookmarkedDiseases = diseaseDao.getBookmarkedDiseases().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // لیست داروهای نشان‌شده
    val bookmarkedDrugs = drugDao.getBookmarkedDrugs().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // توابع برای تغییر وضعیت نشان (ستاره)
    fun toggleDiseaseBookmark(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            diseaseDao.updateBookmark(id, !currentStatus)
        }
    }

    fun toggleDrugBookmark(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            drugDao.updateBookmark(id, !currentStatus)
        }
    }
}