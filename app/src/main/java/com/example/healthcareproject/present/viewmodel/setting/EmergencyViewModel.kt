package com.example.healthcareproject.present.viewmodel.setting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.EmergencyInfo
import com.example.healthcareproject.domain.model.Relationship
import com.example.healthcareproject.domain.usecase.emergencyinfo.EmergencyInfoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val emergencyInfoUseCases: EmergencyInfoUseCases
) : ViewModel() {

    sealed class ContactsUiState {
        object Loading : ContactsUiState()
        data class Success(val contacts: List<EmergencyInfo>) : ContactsUiState()
        data class Error(val message: String) : ContactsUiState()
    }

    private val _contacts = MutableLiveData<ContactsUiState>()
    val contacts: LiveData<ContactsUiState> get() = _contacts

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isEmpty = MutableLiveData<Boolean>(false)
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    private val _availablePriorities = MutableLiveData<List<Int>>()
    val availablePriorities: LiveData<List<Int>> get() = _availablePriorities

    val emergencyName = MutableLiveData<String>()
    val emergencyPhone = MutableLiveData<String>()
    private val _selectedRelationship = MutableLiveData<Relationship>()
    val selectedRelationship: LiveData<Relationship> get() = _selectedRelationship
    private val _selectedPriority = MutableLiveData<Int>()
    val selectedPriority: LiveData<Int> get() = _selectedPriority

    private var editingEmergencyId: String? = null

    init {
        loadContacts()
    }

    fun loadContacts() {
        _contacts.value = ContactsUiState.Loading
        _isLoading.value = true
        _isEmpty.value = false
        viewModelScope.launch {
            try {
                val contacts = emergencyInfoUseCases.getEmergencyInfos()
                _contacts.value = ContactsUiState.Success(contacts.sortedBy { it.priority })
                _isLoading.value = false
                _isEmpty.value = contacts.isEmpty()
                updateAvailablePriorities(contacts, null)
                Timber.tag("EmergencyViewModel").d("Loaded contacts: ${contacts.size}")
            } catch (e: Exception) {
                _contacts.value = ContactsUiState.Error(e.message ?: "Error loading contacts")
                _isLoading.value = false
                _isEmpty.value = false
                Timber.tag("EmergencyViewModel").e(e, "Error loading contacts: ${e.message}")
            }
        }
    }

    fun addOrUpdateContact() {
        val name = emergencyName.value?.trim() ?: return
        val phone = emergencyPhone.value?.trim() ?: return
        val relationship = selectedRelationship.value ?: Relationship.Other
        val priority = selectedPriority.value ?: return

        viewModelScope.launch {
            try {
                if (editingEmergencyId == null) {
                    emergencyInfoUseCases.createEmergencyInfo(
                        contactName = name,
                        contactNumber = phone,
                        relationship = relationship,
                        priority = priority
                    )
                    Timber.tag("EmergencyViewModel")
                        .d("Added contact: $name with priority $priority")
                } else {
                    emergencyInfoUseCases.updateEmergencyInfo(
                        emergencyInfoId = editingEmergencyId!!,
                        contactName = name,
                        contactNumber = phone,
                        relationship = relationship,
                        priority = priority
                    )
                    Timber.tag("EmergencyViewModel")
                        .d("Updated contact: $name with priority $priority")
                }
                resetDialogInputs()
                loadContacts()
            } catch (e: Exception) {
                _contacts.value = ContactsUiState.Error(e.message ?: "Error saving contact")
                _isLoading.value = false
                _isEmpty.value = false
                Timber.tag("EmergencyViewModel").e(e, "Error saving contact: ${e.message}")
            }
        }
    }

    fun deleteContact(emergencyId: String) {
        viewModelScope.launch {
            try {
                emergencyInfoUseCases.deleteEmergencyInfo(emergencyId)
                Timber.tag("EmergencyViewModel").d("Deleted contact: $emergencyId")
                loadContacts()
            } catch (e: Exception) {
                _contacts.value = ContactsUiState.Error(e.message ?: "Error deleting contact")
                _isLoading.value = false
                _isEmpty.value = false
                Timber.tag("EmergencyViewModel").e(e, "Error deleting contact: ${e.message}")
            }
        }
    }

    fun prepareEditContact(emergencyId: String) {
        viewModelScope.launch {
            try {
                val contact = emergencyInfoUseCases.getEmergencyInfoById(emergencyId)
                contact?.let {
                    editingEmergencyId = it.emergencyId
                    emergencyName.value = it.emergencyName
                    emergencyPhone.value = it.emergencyPhone
                    _selectedRelationship.value = it.relationship
                    _selectedPriority.value = it.priority
                    // Update available priorities, including the current contact's priority
                    val contacts = emergencyInfoUseCases.getEmergencyInfos()
                    updateAvailablePriorities(contacts, it.priority)
                    Timber.tag("EmergencyViewModel")
                        .d("Prepared edit for contact: ${it.emergencyName}")
                }
            } catch (e: Exception) {
                _contacts.value = ContactsUiState.Error(e.message ?: "Error loading contact")
                _isLoading.value = false
                _isEmpty.value = false
                Timber.tag("EmergencyViewModel").e(e, "Error preparing edit: ${e.message}")
            }
        }
    }

    private fun updateAvailablePriorities(contacts: List<EmergencyInfo>, editingPriority: Int?) {
        val usedPriorities = contacts.map { it.priority }.toSet()
        // Include editing contact's priority if editing
        val available = (1..5).filter { priority ->
            priority !in usedPriorities || priority == editingPriority
        }
        _availablePriorities.value = available
        Timber.tag("EmergencyViewModel").d("Available priorities: $available")
    }

    fun setRelationship(relationship: Relationship) {
        _selectedRelationship.value = relationship
    }

    fun setPriority(priority: Int) {
        if (priority in 1..5) {
            _selectedPriority.value = priority
        }
    }

    fun resetDialogInputs() {
        editingEmergencyId = null
        emergencyName.value = ""
        emergencyPhone.value = ""
        _selectedRelationship.value = Relationship.Other
        _selectedPriority.value = _availablePriorities.value?.firstOrNull() ?: 5
    }
}