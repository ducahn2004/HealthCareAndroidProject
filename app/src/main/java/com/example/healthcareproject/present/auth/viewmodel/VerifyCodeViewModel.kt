package com.example.healthcareproject.present.auth.viewmodel

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.auth.SendVerificationCodeUseCase
import com.example.healthcareproject.domain.usecase.auth.VerifyCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase
) : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _authFlow = MutableLiveData<AuthFlow>()
    val authFlow: LiveData<AuthFlow> get() = _authFlow

    private val _verificationCode = MutableLiveData<String>()
    val verificationCode: LiveData<String> get() = _verificationCode

    private val _isVerified = MutableLiveData<Boolean>()
    val isVerified: LiveData<Boolean> get() = _isVerified

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _verificationCodeError = MutableLiveData<String?>()
    val verificationCodeError: LiveData<String?> get() = _verificationCodeError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _navigateToCreateNewPassword = MutableLiveData<Boolean>()
    val navigateToCreateNewPassword: LiveData<Boolean> get() = _navigateToCreateNewPassword

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> get() = _navigateToLogin

    private val _timerCount = MutableLiveData<Int>()
    val timerCount: LiveData<Int> get() = _timerCount

    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> get() = _timerText

    private var timerJob: Job? = null

    /**
     * Sets the email and authentication flow for verification.
     */
    fun setEmailAndAuthFlow(email: String, authFlow: AuthFlow) {
        _email.value = email
        _authFlow.value = authFlow
    }

    /**
     * Updates the verification code input.
     */
    fun afterVerificationCodeChange(code: Editable) {
        _verificationCode.value = code.toString()
        _verificationCodeError.value = null
    }

    /**
     * Verifies the entered code for the email.
     */
    fun verifyCode() {
        val code = _verificationCode.value ?: ""
        if (code.isEmpty()) {
            _verificationCodeError.value = "Verification code cannot be empty"
            return
        }

        val email = _email.value ?: run {
            _error.value = "Email is missing"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                verifyCodeUseCase(email, code)
                _isVerified.value = true
                _error.value = null
                _verificationCodeError.value = null
                if (_authFlow.value == AuthFlow.FORGOT_PASSWORD) {
                    _navigateToCreateNewPassword.value = true
                } else {
                    _navigateToLogin.value = true
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Invalid verification code"
                _isVerified.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sends a new verification code to the email.
     */
    fun sendVerificationCode() {
        val email = _email.value ?: run {
            _error.value = "Email is missing"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                sendVerificationCodeUseCase(email)
                _error.value = null
                startTimer()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to send verification code"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Starts a timer to limit resending verification codes.
     * @param duration The timer duration in seconds (default: 60).
     */
    fun startTimer(duration: Int = 60) {
        stopTimer()
        _timerCount.value = duration
        _timerText.value = "Resend in $duration s"
        timerJob = viewModelScope.launch {
            while (_timerCount.value!! > 0) {
                delay(1000)
                _timerCount.value = _timerCount.value!! - 1
                _timerText.value = if (_timerCount.value!! > 0) "Resend in ${_timerCount.value} s" else "Resend Code"
            }
        }
    }

    /**
     * Stops the resend timer.
     */
    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerCount.value = 0
        _timerText.value = "Resend Code"
    }

    /**
     * Resets navigation states.
     */
    fun resetNavigationStates() {
        _navigateToCreateNewPassword.value = false
        _navigateToLogin.value = false
    }

    /**
     * Sets an error message.
     */
    fun setError(error: String?) {
        _error.value = error
    }

    /**
     * Clears the error message.
     */
    fun clearError() {
        _error.value = null
    }

    enum class AuthFlow {
        REGISTRATION, FORGOT_PASSWORD
    }
}