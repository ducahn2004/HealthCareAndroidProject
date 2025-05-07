package com.example.healthcareproject.present.viewmodel.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.EmergencyInfo
import com.example.healthcareproject.domain.model.Relationship
import com.example.healthcareproject.domain.usecase.emergencyinfo.EmergencyInfoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    // Two-way binding for dialog inputs
    val emergencyName = MutableLiveData<String>()
    val emergencyPhone = MutableLiveData<String>()
    private val _selectedRelationship = MutableLiveData<Relationship>()
    val selectedRelationship: LiveData<Relationship> get() = _selectedRelationship
    private val _selectedPriority = MutableLiveData<Int>()
    val selectedPriority: LiveData<Int> get() = _selectedPriority

    // Track editing state
    private var editingEmergencyId: String? = null

    init {
        loadContacts()
    }

    fun loadContacts() {
        _contacts.value = ContactsUiState.Loading
        viewModelScope.launch {
            try {
                val contacts = emergencyInfoUseCases.getEmergencyInfos()
                _contacts.value = ContactsUiState.Success(contacts.sortedBy { it.priority })
            } catch (e: Exception) {
                _contacts.value = ContactsUiState.Error(e.message ?: "Error loading contacts")
            }
        }
    }

    fun addOrUpdateContact() {
        val name = emergencyName.value?.trim() ?: return
        val phone = emergencyPhone.value?.trim() ?: return
        val relationship = selectedRelationship.value ?: Relationship.Other
        val priority = selectedPriority.value ?: 5

        viewModelScope.launch {
            try {
                if (editingEmergencyId == null) {
                    // Add new contact
                    emergencyInfoUseCases.createEmergencyInfo(
                        contactName = name,
                        contactNumber = phone,
                        relationship = relationship,
                        priority = priority
                    )
                } else {
                    // Update existing contact
                    emergencyInfoUseCases.updateEmergencyInfo(
                        emergencyInfoId = editingEmergencyId!!,
                        contactName = name,
                        contactNumber = phone,
                        relationship = relationship,
                        priority = priority
                    )
                }
                resetDialogInputs()
                loadContacts()
            } catch (e: Exception) {
                _contacts.value = ContactsUiState.Error(e.message ?: "Error saving contact")
            }
        }
    }

    fun deleteContact(emergencyId: String) {
        viewModelScope.launch {
            try {
                emergencyInfoUseCases.deleteEmergencyInfo(emergencyId)
                loadContacts()
            } catch (e: Exception) {
                _contacts.value = ContactsUiState.Error(e.message ?: "Error deleting contact")
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
                }
            } catch (e: Exception) {
                _contacts.value = ContactsUiState.Error(e.message ?: "Error loading contact")
            }
        }
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
        _selectedPriority.value = 5
    }
}