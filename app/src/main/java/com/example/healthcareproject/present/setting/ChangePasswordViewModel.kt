package com.example.healthcareproject.present.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userFirebaseDataSource: UserFirebaseDataSource
) : ViewModel() {

    // Use LiveData for form fields
    private val _currentPassword = MutableLiveData("")
    val currentPassword: LiveData<String> = _currentPassword

    private val _newPassword = MutableLiveData("")
    val newPassword: LiveData<String> = _newPassword

    // For updating values (two-way binding)
    fun setCurrentPassword(value: String) {
        _currentPassword.value = value
    }

    fun setNewPassword(value: String) {
        _newPassword.value = value
    }

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isPasswordChanged = MutableLiveData<Boolean>(false)
    val isPasswordChanged: LiveData<Boolean> = _isPasswordChanged

    fun changePassword(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentPassword = _currentPassword.value?.trim() ?: ""
                val newPassword = _newPassword.value?.trim() ?: ""

                // Validate inputs
                if (currentPassword.isEmpty()) {
                    _error.value = "Current password is required"
                    return@launch
                }
                if (newPassword.isEmpty()) {
                    _error.value = "New password is required"
                    return@launch
                }
                if (newPassword.length < 6) {
                    _error.value = "New password must be at least 6 characters"
                    return@launch
                }

                // Update password via UserFirebaseDataSource
                userFirebaseDataSource.updatePassword(userId, currentPassword, newPassword)

                _isPasswordChanged.value = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to change password"
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        _error.value = null
        _isPasswordChanged.value = false
        super.onCleared()
    }
}