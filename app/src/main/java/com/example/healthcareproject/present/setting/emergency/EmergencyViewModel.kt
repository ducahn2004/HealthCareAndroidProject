package com.example.healthcareproject.present.setting.emergency

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

    private val _contacts = MutableLiveData<ContactsUiState>()
    val contacts: LiveData<ContactsUiState> = _contacts

    private val _dialogState = MutableLiveData<DialogUiState>()
    val dialogState: LiveData<DialogUiState> = _dialogState

    private val _operationState = MutableLiveData<OperationState>()
    val operationState: LiveData<OperationState> = _operationState

    // Databinding properties
    val emergencyName = MutableLiveData<String>()
    val emergencyPhone = MutableLiveData<String>()
    val relationship = MutableLiveData<Relationship>()
    val priority = MutableLiveData<Int>()

    fun fetchEmergencyContacts() {
        viewModelScope.launch {
            _contacts.value = ContactsUiState.Loading
            try {
                val contacts = emergencyInfoUseCases.getEmergencyInfos()
                _contacts.value = ContactsUiState.Success(contacts)
            } catch (e: Exception) {
                _contacts.value = ContactsUiState.Error(e.message ?: "Failed to fetch contacts")
            }
        }
    }

    fun addEmergencyContact() {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            val name = emergencyName.value ?: ""
            val phone = emergencyPhone.value ?: ""
            val rel = relationship.value ?: Relationship.Other
            val prio = priority.value ?: 1

            if (!validateInput(name, phone)) return@launch

            try {
                emergencyInfoUseCases.createEmergencyInfo(
                    contactName = name,
                    contactNumber = phone,
                    relationship = rel,
                    notes = null, // Not used in EmergencyInfo
                    status = prio > 0 // Map priority to status
                )
                _operationState.value = OperationState.Success("Contact added successfully")
                fetchEmergencyContacts()
                // Clear fields after successful add
                clearFields()
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to add contact")
            }
        }
    }

    fun updateEmergencyContact(emergencyId: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            val name = emergencyName.value ?: ""
            val phone = emergencyPhone.value ?: ""
            val rel = relationship.value ?: Relationship.Other
            val prio = priority.value ?: 1

            if (!validateInput(name, phone)) return@launch

            try {
                emergencyInfoUseCases.updateEmergencyInfo(
                    emergencyInfoId = emergencyId,
                    contactName = name,
                    contactNumber = phone,
                    relationship = rel,
                    notes = null, // Not used in EmergencyInfo
                    status = prio > 0 // Map priority to status
                )
                _operationState.value = OperationState.Success("Contact updated successfully")
                fetchEmergencyContacts()
                // Clear fields after successful update
                clearFields()
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to update contact")
            }
        }
    }

    fun deleteEmergencyContact(emergencyId: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                emergencyInfoUseCases.deleteEmergencyInfo(emergencyId)
                _operationState.value = OperationState.Success("Contact deleted successfully")
                fetchEmergencyContacts()
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to delete contact")
            }
        }
    }

    fun setContactForEdit(contact: EmergencyInfo?) {
        if (contact != null) {
            // Set fields for edit
            emergencyName.value = contact.emergencyName
            emergencyPhone.value = contact.emergencyPhone
            relationship.value = contact.relationship
            priority.value = contact.priority
            _dialogState.value = DialogUiState.EditContact(contact)
        } else {
            // Clear fields for add
            clearFields()
            _dialogState.value = DialogUiState.AddContact
        }
    }

    private fun clearFields() {
        emergencyName.value = ""
        emergencyPhone.value = ""
        relationship.value = Relationship.Other
        priority.value = 1
    }

    private fun validateInput(name: String, phone: String): Boolean {
        var isValid = true
        if (name.isEmpty()) {
            _dialogState.value = DialogUiState.Error("Name is required")
            isValid = false
        }
        if (phone.isEmpty()) {
            _dialogState.value = DialogUiState.Error("Phone number is required")
            isValid = false
        } else if (!phone.matches(Regex("^[0-9]{10,15}$"))) {
            _dialogState.value = DialogUiState.Error("Invalid phone number")
            isValid = false
        }
        return isValid
    }

    sealed class ContactsUiState {
        object Loading : ContactsUiState()
        data class Success(val contacts: List<EmergencyInfo>) : ContactsUiState()
        data class Error(val message: String) : ContactsUiState()
    }

    sealed class DialogUiState {
        object AddContact : DialogUiState()
        data class EditContact(val contact: EmergencyInfo) : DialogUiState()
        data class Error(val message: String) : DialogUiState()
    }

    sealed class OperationState {
        object Loading : OperationState()
        data class Success(val message: String) : OperationState()
        data class Error(val message: String) : OperationState()
    }
}