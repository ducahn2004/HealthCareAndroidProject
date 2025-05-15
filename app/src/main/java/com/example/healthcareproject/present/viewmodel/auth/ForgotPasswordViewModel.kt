package com.example.healthcareproject.present.viewmodel.auth

import android.text.Editable
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.auth.SendVerificationCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase
) : ViewModel() {

    private val _isCodeSent = MutableLiveData<Boolean>()
    val isCodeSent: LiveData<Boolean> get() = _isCodeSent

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _navigateToVerifyCode = MutableLiveData<Boolean>()
    val navigateToVerifyCode: LiveData<Boolean> = _navigateToVerifyCode

    private val _resetRequestSuccess = MutableLiveData<String?>()
    val resetRequestSuccess: LiveData<String?> = _resetRequestSuccess

    fun afterEmailChange(value: Editable){
        _email.value = value.toString()
    }


    fun onResetPasswordClicked() {
        val emailValue = email.value ?: ""

        _emailError.value = null
        _error.value = null

        if (emailValue.isBlank()) {
            _emailError.value = "Email is required"
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = "Invalid email format"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                Timber.d("Attempting to send verification code for password reset to: $emailValue")
                sendVerificationCodeUseCase(emailValue)
                _isCodeSent.value = true
                _resetRequestSuccess.value = "Password reset requested! Check your email for the verification code."
                _navigateToVerifyCode.value = true
                _error.value = null
                Timber.d("Verification code sent successfully for password reset")
            } catch (e: Exception) {
                Timber.e(e, "Failed to send verification code for password reset")
                _error.value = e.message ?: "Failed to send verification code"
                _resetRequestSuccess.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetNavigationStates() {
        _navigateToVerifyCode.value = false
        _resetRequestSuccess.value = null
    }

    fun clearError() {
        _error.value = null
    }
}