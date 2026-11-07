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
    
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners

    fun loadBanners(position: String, userCity: String) {
        viewModelScope.launch {
            val fetchedBanners = repository.fetchBanners(position, userCity)
            _banners.value = fetchedBanners
        }
    }
}