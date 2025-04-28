package com.example.healthcareproject.present.auth.viewmodel

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNewPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {  private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _newPassword = MutableLiveData<String>()
    val newPassword: LiveData<String> = _newPassword

    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String> = _confirmPassword

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    fun setEmail(value: String) {
        _email.value = value
    }



    fun afterNewPasswordChange(value: Editable){
        _newPassword.value = value.toString()
    }

    fun afterConfirmPassword(value: Editable){
        _confirmPassword.value = value.toString()
    }

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
        if (newPasswordValue.length < 8) {
            _error.value = "Password must be at least 8 characters"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                resetPasswordUseCase(emailValue, newPasswordValue)
                _navigateToLogin.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to reset password"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetNavigationStates() {
        _navigateToLogin.value = false
    }
}