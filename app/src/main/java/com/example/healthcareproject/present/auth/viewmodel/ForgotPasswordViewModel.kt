package com.example.healthcareproject.present.auth.viewmodel

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.data.source.network.datasource.UserFirebaseDataSource
import com.example.healthcareproject.domain.usecase.auth.SendVerificationCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _emailError.value = "Invalid email format"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                sendVerificationCodeUseCase(emailValue)
                _isCodeSent.value = true
                _navigateToVerifyCode.value = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to send verification code"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetNavigationStates() {
        _navigateToVerifyCode.value = false
    }

    fun clearError() {
        _error.value = null
    }
}