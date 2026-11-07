package com.viapezeshk.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viapezeshk.app.data.Banner
import com.viapezeshk.app.data.BannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BannerViewModel : ViewModel() {
    private val repository = BannerRepository()
    
    // لیست بنرها رو به صورت زنده نگه می‌داریم
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners

    init {
        fetchBanners()
    }

    private fun fetchBanners() {
        viewModelScope.launch {
            val fetchedBanners = repository.fetchBanners()
            // مرتب‌سازی بر اساس اولویت (priority)
            _banners.value = fetchedBanners.sortedBy { it.priority }
        }
    }
}