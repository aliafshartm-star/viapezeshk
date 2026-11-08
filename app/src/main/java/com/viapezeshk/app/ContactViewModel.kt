package com.viapezeshk.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viapezeshk.app.data.ContactItem
import com.viapezeshk.app.data.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactViewModel : ViewModel() {
    private val repository = ContactRepository()
    
    private val _contacts = MutableStateFlow<List<ContactItem>>(emptyList())
    val contacts: StateFlow<List<ContactItem>> = _contacts

    fun loadContacts() {
        viewModelScope.launch {
            val fetchedContacts = repository.fetchContacts()
            _contacts.value = fetchedContacts
        }
    }
}