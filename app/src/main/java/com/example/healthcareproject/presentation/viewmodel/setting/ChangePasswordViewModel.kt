package com.example.healthcareproject.presentation.viewmodel.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.auth.UpdatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling password change logic in the Change Password flow.
 */
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : ViewModel() {

    /**
     * Current password input.
     */
    private val _currentPassword = MutableLiveData("")
    val currentPassword: LiveData<String> = _currentPassword

    /**
     * New password input.
     */
    private val _newPassword = MutableLiveData("")
    val newPassword: LiveData<String> = _newPassword

    /**
     * Confirm new password input.
     */
    private val _confirmPassword = MutableLiveData("")
    val confirmPassword: LiveData<String> = _confirmPassword

    /**
     * Error message for UI display.
     */
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Loading state for UI.
     */
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Indicates if the password was changed successfully.
     */
    private val _isPasswordChanged = MutableLiveData<Boolean>(false)
    val isPasswordChanged: LiveData<Boolean> = _isPasswordChanged

    /**
     * Sets the current password input.
     */
    fun setCurrentPassword(value: String) {
        _currentPassword.value = value
    }

    /**
     * Sets the new password input.
     */
    fun setNewPassword(value: String) {
        _newPassword.value = value
    }

    /**
     * Sets the confirm password input.
     */
    fun setConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    /**
     * Initiates the password change process.
     * @param userId The user's email address.
     */
    fun changePassword(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentPassword = _currentPassword.value?.trim() ?: ""
                val newPassword = _newPassword.value?.trim() ?: ""
                val confirmPassword = _confirmPassword.value?.trim() ?: ""

                // Validate inputs
                if (currentPassword.isEmpty()) {
                    _error.value = "Current password is required"
                    return@launch
                }
                if (newPassword.isEmpty()) {
                    _error.value = "New password is required"
                    return@launch
                }
                if (confirmPassword.isEmpty()) {
                    _error.value = "Confirm password is required"
                    return@launch
                }
                if (newPassword != confirmPassword) {
                    _error.value = "Passwords do not match"
                    return@launch
                }
                if (!isStrongPassword(newPassword)) {
                    _error.value = "Password must be at least 8 characters, including uppercase, lowercase, number, and special character"
                    return@launch
                }

                // Update password via UpdatePasswordUseCase
                updatePasswordUseCase(userId, currentPassword, newPassword)

                _isPasswordChanged.value = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to change password"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Checks if the password meets strength requirements.
     */
    private fun isStrongPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
        return regex.matches(password)
    }

    override fun onCleared() {
        _error.value = null
        _isPasswordChanged.value = false
        super.onCleared()
    }
}