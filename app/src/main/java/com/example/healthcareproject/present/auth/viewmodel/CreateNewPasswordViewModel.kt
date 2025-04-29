package com.example.healthcareproject.present.auth.viewmodel

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.auth.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling password reset logic in the Forgot Password flow.
 */
@HiltViewModel
class CreateNewPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _newPassword = MutableLiveData<String>()
    val newPassword: LiveData<String> = _newPassword

    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String> = _confirmPassword

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _success = MutableLiveData<String?>()
    val success: LiveData<String?> = _success

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    /**
     * Sets the email for password reset.
     */
    fun setEmail(value: String) {
        _email.value = value
    }

    /**
     * Updates the new password input.
     */
    fun afterNewPasswordChange(value: Editable) {
        _newPassword.value = value.toString()
    }

    /**
     * Updates the confirm password input.
     */
    fun afterConfirmPassword(value: Editable) {
        _confirmPassword.value = value.toString()
    }

    /**
     * Handles the reset password button click, validates inputs, and initiates password reset.
     */
    fun onResetPasswordClicked() {
        val emailValue = email.value ?: ""
        val newPasswordValue = newPassword.value ?: ""
        val confirmPasswordValue = confirmPassword.value ?: ""

        _error.value = null

        if (emailValue.isBlank()) {
            _error.value = "Email is required to reset password"
            return
        }
        if (newPasswordValue.isBlank()) {
            _error.value = "New password is required"
            return
        }
        if (confirmPasswordValue.isBlank()) {
            _error.value = "Confirm password is required"
            return
        }
        if (newPasswordValue != confirmPasswordValue) {
            _error.value = "Passwords do not match"
            return
        }
        if (!isStrongPassword(newPasswordValue)) {
            _error.value = "Password must be at least 8 characters, including uppercase, lowercase, number, and special character"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                resetPasswordUseCase(emailValue, newPasswordValue)
                _success.value = "Password reset successfully"
                _navigateToLogin.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to reset password"
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

    /**
     * Resets navigation states.
     */
    fun resetNavigationStates() {
        _navigateToLogin.value = false
        _success.value = null
        _error.value = null
    }
}