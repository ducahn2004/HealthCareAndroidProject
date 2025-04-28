package com.example.healthcareproject.present.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import com.example.healthcareproject.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Data class for two-way binding
    inner class PasswordForm : BaseObservable() {
        @get:Bindable
        var currentPassword: String = ""
            set(value) {
                if (field != value) {
                    field = value
                    notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.currentPassword)
                }
            }

        @get:Bindable
        var newPassword: String = ""
            set(value) {
                if (field != value) {
                    field = value
                    notifyPropertyChanged(androidx.databinding.library.baseAdapters.BR.newPassword)
                }
            }
    }

    // Instance of PasswordForm for binding
    val passwordForm = PasswordForm()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isPasswordChanged = MutableLiveData<Boolean>(false)
    val isPasswordChanged: LiveData<Boolean> get() = _isPasswordChanged

    fun changePassword(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentPassword = passwordForm.currentPassword.trim()
                val newPassword = passwordForm.newPassword.trim()

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