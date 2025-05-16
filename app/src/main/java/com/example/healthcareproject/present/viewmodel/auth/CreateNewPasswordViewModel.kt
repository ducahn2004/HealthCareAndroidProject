package com.example.healthcareproject.present.viewmodel.auth

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.auth.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

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

    fun setEmail(value: String) {
        _email.value = value
    }

    fun afterNewPasswordChange(value: Editable) {
        _newPassword.value = value.toString()
    }

    fun afterConfirmPassword(value: Editable) {
        _confirmPassword.value = value.toString()
    }

    fun onResetPasswordClicked() {
        Timber.d("onResetPasswordClicked called")
        val emailValue = email.value ?: ""
        val newPasswordValue = newPassword.value ?: ""
        val confirmPasswordValue = confirmPassword.value ?: ""

        _error.value = null
        Timber.d("Email: $emailValue, New Password: $newPasswordValue, Confirm Password: $confirmPasswordValue")

        if (emailValue.isBlank()) {
            Timber.w("Validation failed: Email is blank")
            _error.value = "Email is required to reset password"
            return
        }
        if (newPasswordValue.isBlank()) {
            Timber.w("Validation failed: New password is blank")
            _error.value = "New password is required"
            return
        }
        if (confirmPasswordValue.isBlank()) {
            Timber.w("Validation failed: Confirm password is blank")
            _error.value = "Confirm password is required"
            return
        }
        if (newPasswordValue != confirmPasswordValue) {
            Timber.w("Validation failed: Passwords do not match")
            _error.value = "Passwords do not match"
            return
        }
        if (!isStrongPassword(newPasswordValue)) {
            Timber.w("Validation failed: Password is not strong enough")
            _error.value = "Password must be at least 8 characters, including uppercase, lowercase, number, and special character"
            return
        }

        _isLoading.value = true
        Timber.d("Calling resetPasswordUseCase with email: $emailValue")
        viewModelScope.launch {
            try {
                resetPasswordUseCase(emailValue, newPasswordValue)
                Timber.d("Password reset successful")
                _success.value = "Password reset successfully"
                _navigateToLogin.value = true
            } catch (e: Exception) {
                Timber.e(e, "Failed to reset password: ${e.message}")
                _error.value = e.message ?: "Failed to reset password"
            } finally {
                _isLoading.value = false
                Timber.d("Reset password operation completed")
            }
        }
    }

    private fun isStrongPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
        return regex.matches(password)
    }

    fun resetNavigationStates() {
        _navigateToLogin.value = false
        _success.value = null
        _error.value = null
    }
}